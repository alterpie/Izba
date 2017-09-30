package com.yerastov.android.izba.ui.person

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.yerastov.android.izba.R
import com.yerastov.android.izba.data.DataManager
import com.yerastov.android.izba.domain.Person
import com.yerastov.android.izba.getText
import com.yerastov.android.izba.hideKeyboard
import com.yerastov.android.izba.isTextEmpty
import com.yerastov.android.izba.ui.base.BaseDialog
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_add_person.*
import kotlinx.android.synthetic.main.progress.*

/**
 * Created by yerastov on 26/09/17.
 */


internal class AddPersonDialog : BaseDialog(), View.OnClickListener {

    private lateinit var listener: AddPersonListener

    companion object {
        fun newInstance(): AddPersonDialog {
            val dialog = AddPersonDialog()
            dialog.isCancelable = false
            val args = Bundle()

            dialog.arguments = args
            return dialog
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (parentFragment is AddPersonListener) {
            listener = parentFragment as AddPersonListener
        } else {
            throw RuntimeException()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                if (validateInputs()) {
                    val person = Person(til_input_person.getText(), til_device.getText())
                    savePerson(person)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        progress.visibility = View.GONE
                                        listener.onPersonAdded(person)
                                        dismiss()
                                    })
                }
            }

            R.id.btn_cancel -> {
                dismiss()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (til_input_person.isTextEmpty())
            til_input_person.error = getString(R.string.error_field_required)
        else
            til_input_person.error = null

        if (til_device.isTextEmpty())
            til_device.error = getString(R.string.error_field_required)
        else til_device.error = null

        return til_input_person.error == null && til_device.error == null
    }


    override fun initViewListeners() {
        btn_add.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun getLayoutId(): Int = R.layout.dialog_add_person

    private fun savePerson(person: Person): Completable {
        hideKeyboard()
        progress.visibility = View.VISIBLE
        return Completable.create({
            try {
                DataManager.savePerson(person, activity)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        })
    }

}