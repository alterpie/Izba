package com.yerastov.android.izba

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.yerastov.android.izba.data.PrefsManager
import com.yerastov.android.izba.utils.Const
import java.util.*

/**
 * Created by yerastov on 22/09/17.
 */

//fun AppCompatActivity.replaceFragment(fragment: Fragment, tag: String? = null) {
//    supportFragmentManager.beginTransaction()
//            .replace(R.id.container, fragment, tag)
//            .commit()
//}

fun Context.toast(message: String?, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.getPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

fun DialogFragment.hideKeyboard() {
    activity.currentFocus?.let {
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Fragment.hideKeyboard() {
    activity.currentFocus?.let {
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Context.changeLanguage() {
    val locale = Locale(PrefsManager.getLocale(this))
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun TextInputLayout.getText(): String = editText?.text.toString()

fun TextInputLayout.isTextEmpty(): Boolean = editText?.length() == 0

fun TextInputLayout.setText(text: String) {
    editText?.apply {
        setText(text)
        setSelection(length())
    }
}

fun Fragment.checkStoragePermissions(func: () -> Unit) {
    if (isMoreM()) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), Const.REQUEST_WRITE_STORAGE)
//            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), Const.REQUEST_WRITE_STORAGE)
        } else {
            func()
        }
    } else {
        func()
    }
}

fun isMoreM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun TextView.getString(): String = text.toString()

object DefaultTextChangeListener : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}