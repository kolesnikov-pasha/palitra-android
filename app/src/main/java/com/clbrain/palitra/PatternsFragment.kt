package com.clbrain.palitra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.clbrain.palitra.Pattern.Companion.PATTERNS

class PatternsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.patterns_fragment, container, false)
        val viewId = intArrayOf(
            R.id.bottom_triptych,
            R.id.corner_triptych,
            R.id.right_triptych,
            R.id.four_lines,
            R.id.bottom_polyptych4,
            R.id.bottom_polyptych5
        )
        for (i in PATTERNS.indices) {
            val imageView = view.findViewById<ImageView>(viewId[i])
            imageView.setOnClickListener {
                (context as MainActivity).mainPalitra.setPattern(PATTERNS[i])
            }
        }
        return view
    }

    companion object {
        fun create(): PatternsFragment {
            return PatternsFragment()
        }
    }
}