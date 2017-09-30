package com.yerastov.android.izba

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.yerastov.android.izba.data.PrefsManager
import com.yerastov.android.izba.ui.person.PersonFragment
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PrefsManager.getLocale(this).toLowerCase() != Locale.getDefault().language.toLowerCase()) {
            changeLanguage()
            recreate()
        }
        setContentView(R.layout.activity_main)
        replaceFragment(PersonFragment.newInstance(null)/* getFragmentByTag(TAG_LOGIN)*/)
    }

    fun replaceFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
    }

    private var doubleBackToExitPressedOnce: Boolean = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) super.onBackPressed() else toast(getString(R.string.click_twice_to_exit))
        doubleBackToExitPressedOnce = true
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1500)
    }
}
