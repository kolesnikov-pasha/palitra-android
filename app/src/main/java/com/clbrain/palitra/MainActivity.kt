package com.clbrain.palitra

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.clbrain.palitra.palitra_view.PalitraView
import com.google.android.material.tabs.TabLayout
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {
    private lateinit var btnAddImage: TextView
    private lateinit var btnSave: TextView
    private var currentPicture: Bitmap? = null
    lateinit var mainPalitra: PalitraView
    private lateinit var layout: RelativeLayout
    private lateinit var pager: ViewPager
    private lateinit var tabs: TabLayout

    private var dialogCanceled = false
    private var dialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            ),
            1
        )
        layout = findViewById(R.id.main_activity_layout)
        pager = findViewById(R.id.pager)
        tabs = findViewById(R.id.tabs)
        pager.adapter = PagerAdapter(supportFragmentManager)
        tabs.setupWithViewPager(pager)
        btnAddImage = findViewById(R.id.btn_add_img)
        btnAddImage.setOnClickListener {
            callNewPhotoDialog()
        }
        btnSave = findViewById(R.id.btn_save)
        btnSave.setOnClickListener {
            callSaveDialog()
        }
        mainPalitra = findViewById(R.id.palitra)
    }

    private fun callSaveDialog() {
        val dialogBuilder = AlertDialog.Builder(this@MainActivity)
        dialogBuilder.setMessage("Do you want to save?")
        dialogBuilder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            mainPalitra.setCurrentImage(null)
            if (dialogOpen) {
                addImage()
            }
            dialogOpen = false
            dialog.cancel()
        }
        dialogBuilder.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            mainPalitra.save()
            mainPalitra.setCurrentImage(null)
            if (dialogOpen) {
                addImage()
            }
            dialogOpen = false
            dialog.cancel()
        }
        dialogBuilder.setOnCancelListener {
            dialogCanceled = true
        }
        if (mainPalitra.canBeSaved()) {
            dialogBuilder.create().show()
        } else {
            if (dialogOpen && !dialogCanceled) {
                addImage()
                dialogOpen = false
            }
            dialogCanceled = false
        }
    }

    fun addImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun callNewPhotoDialog() {
        dialogOpen = true
        callSaveDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (data != null) {
                    val targetUri = data.data ?: return
                    try {
                        currentPicture = BitmapFactory.decodeStream(contentResolver.openInputStream(targetUri))
                        mainPalitra.setCurrentImage(currentPicture)
                        Toast.makeText(applicationContext, "OK!!!", Toast.LENGTH_SHORT).show()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private companion object {
        const val GALLERY_REQUEST_CODE = 1951
    }
}