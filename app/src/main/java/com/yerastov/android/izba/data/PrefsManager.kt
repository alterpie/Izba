package com.yerastov.android.izba.data

import android.content.Context
import com.yerastov.android.izba.domain.Language
import com.yerastov.android.izba.getPreferences

/**
 * Created by yerastov on 28/09/17.
 */
object PrefsManager {

    private val SUPPLIER_ID = "SUPPLIER_ID"
    private val ITEM_ID = "ITEM_ID"
    private val LOCALE = "LOCALE"

    fun generateSupplierId(device: String, context: Context): String {
        val preferences = context.getPreferences()
        val id = preferences.getLong(SUPPLIER_ID, 1)
        preferences.edit().putLong(SUPPLIER_ID, id + 1).apply()
        return device + "_" + formatToSixDigits(id)
    }

    private fun formatToSixDigits(id: Long): String {
        val length = id.toString().length
        var str = id.toString()
        for (i in 0..(5 - length)) {
            str = '0' + str
        }
        return str
    }

    fun setLocale(@Language.Locale locale: String, context: Context) {
        val preferences = context.getPreferences()
        preferences.edit().putString(LOCALE, locale).apply()
    }

    fun getLocale(context: Context): String = context.getPreferences().getString(LOCALE, Language.ENGLISH)

    fun generateItemId(context: Context): String {
        val preferences = context.getPreferences()
        val id = preferences.getLong(ITEM_ID, 1)
        preferences.edit().putLong(ITEM_ID, id + 1).apply()

        return id.toString()
    }
}