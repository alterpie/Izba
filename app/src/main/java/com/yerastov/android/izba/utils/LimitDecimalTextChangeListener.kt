package com.yerastov.android.izba.utils

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import com.yerastov.android.izba.DefaultTextChangeListener
import com.yerastov.android.izba.setText

/**
 * Created by yerastov on 29/09/17.
 */
class LimitDecimalTextChangeListener(private val decimalLimit: Int,
                                     private val til: TextInputLayout) : TextWatcher by DefaultTextChangeListener {
    var ignore: Boolean = false
    var oldText: String = ""

    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        val split = text.split(".")
        if (!ignore) {
            if ((text.contains(".")
                    && oldText.contains(".")
                    && '.' == text.lastOrNull()
                    && oldText.length < text.length)
                    || (split.size > 1 && split[1].length == decimalLimit + 1)) {
                ignore = true
                til.setText(oldText)
                ignore = false
            } else {
                oldText = text
            }
        }
    }
}