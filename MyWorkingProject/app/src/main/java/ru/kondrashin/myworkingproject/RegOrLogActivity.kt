package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment


import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager


class RegOrLogActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager
    companion object{
        fun newIntent(packageContext: Context?): Intent? {
            val intent = Intent(packageContext, RegOrLogActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regorlog)
        viewPager = findViewById(R.id.viewPager)
        val pagerAdapter = AuthenticationPagerAdapter(
            supportFragmentManager
        )
        pagerAdapter.addFragmet(LoginFragment())
        pagerAdapter.addFragmet(RegistrationFragment())
        viewPager.setAdapter(pagerAdapter)





    }
    internal class AuthenticationPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        private val fragmentList: ArrayList<Fragment> = ArrayList()

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(i: Int): Fragment {
            return fragmentList[i]
        }

        fun addFragmet(fragment: Fragment) {
            fragmentList.add(fragment)
        }
    }
}


