package com.clbrain.palitra

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            PatternsFragment.create()
        } else {
            SettingsFragment.create()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TITLES[position]
    }

    companion object {
        private val TITLES = arrayOf("LAYOUT", "TOOLS")
    }
}