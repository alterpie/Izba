package com.yerastov.android.izba.utils

import android.text.Editable
import android.text.TextWatcher
import com.yerastov.android.izba.DefaultTextChangeListener

/**
 * Created by yerastov on 28/09/17.
 */
class TextChangeListener(private val func: (editable: Editable?) -> Unit) : TextWatcher by DefaultTextChangeListener {
    override fun afterTextChanged(p0: Editable?) {
        func(p0)
    }
}