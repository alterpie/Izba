package com.yerastov.android.izba.ui.supplier

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.yerastov.android.izba.*
import com.yerastov.android.izba.data.DataManager
import com.yerastov.android.izba.data.PrefsManager
import com.yerastov.android.izba.domain.Person
import com.yerastov.android.izba.domain.Supplier
import com.yerastov.android.izba.ui.base.BaseDialog
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_add_supplier.*
import kotlinx.android.synthetic.main.progress.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yerastov on 27/09/17.
 */
internal class AddSupplierDialog : BaseDialog(), View.OnClickListener {

    private lateinit var listener: AddSupplierListener

    companion object {
        const val KEY_PERSON = "KEY_PERSON"
        fun newInstance(person: Person): AddSupplierDialog {
            val dialog = AddSupplierDialog()
            val args = Bundle()
            args.putParcelable(KEY_PERSON, person)
            dialog.arguments = args
            dialog.isCancelable = false
            return dialog
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (parentFragment is AddSupplierListener) {
            listener = parentFragment as AddSupplierListener
        }
    }

    override fun initViewListeners() {
        btn_add.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun getLayoutId(): Int = R.layout.dialog_add_supplier


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                if (validateInput()) {
                    val person = arguments.getParcelable<Person>(KEY_PERSON)
                    val supplier = Supplier(til_supplier.getText(),
                            PrefsManager.generateSupplierId(person.device, activity),
                            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date()),
                            person.name)
                    saveSupplier(supplier)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        hideKeyboard()
                                        listener.onSupplierAdded(supplier)
                                        dismiss()
                                    },
                                    {
                                        progress.visibility = View.GONE
                                        activity.toast(it.message)
                                    })
                }
            }
            R.id.btn_cancel -> dismiss()
        }
    }

    private fun validateInput(): Boolean {
        if (til_supplier.isTextEmpty())
            til_supplier.error = getString(R.string.error_field_required)
        else til_supplier.error = null
        return til_supplier.error == null
    }

    private fun saveSupplier(supplier: Supplier): Completable {
        progress.visibility = View.VISIBLE
        return Completable.create({
            try {
                DataManager.saveSupplier(supplier, activity)
                it.onComplete()
            } catch (ex: Exception) {
                it.onError(ex)
            }
        })
    }
}