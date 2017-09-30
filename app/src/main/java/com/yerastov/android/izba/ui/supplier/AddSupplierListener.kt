package com.yerastov.android.izba.ui.supplier

import com.yerastov.android.izba.domain.Supplier

/**
 * Created by yerastov on 27/09/17.
 */
internal interface AddSupplierListener {

    fun onSupplierAdded(supplier: Supplier)
}