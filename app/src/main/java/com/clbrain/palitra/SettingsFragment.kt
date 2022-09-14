package com.clbrain.palitra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentView = inflater.inflate(R.layout.settings_fragment, container, false)
        val switchView = currentView.findViewById<SwitchCompat>(R.id.switch_hex)
        switchView.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            (context as MainActivity).mainPalitra.setWriteHEX(isChecked)
        }
        return currentView
    }

    companion object {
        fun create(): SettingsFragment {
            return SettingsFragment()
        }
    }
}