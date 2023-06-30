package com.example.openinapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.openinapp.Fragment.RecentLinksFragment
import com.example.openinapp.Fragment.TopLinksFragment

class MyFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> RecentLinksFragment()
            else -> TopLinksFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
