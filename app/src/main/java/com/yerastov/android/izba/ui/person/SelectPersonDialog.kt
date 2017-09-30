package com.yerastov.android.izba.ui.person

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.yerastov.android.izba.R
import com.yerastov.android.izba.data.DataManager
import com.yerastov.android.izba.domain.Person
import com.yerastov.android.izba.hideKeyboard
import com.yerastov.android.izba.ui.base.BaseDialog
import com.yerastov.android.izba.utils.TextChangeListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_select_person.*
import kotlinx.android.synthetic.main.progress.*

/**
 * Created by yerastov on 27/09/17.
 */
internal class SelectPersonDialog : BaseDialog(), View.OnClickListener {

    private lateinit var listener: SelectPersonListener
    private val adapter: PersonAdapter = PersonAdapter(this::personSelected)
    private lateinit var disposable: Disposable

    companion object {
        fun newInstance(): SelectPersonDialog {
            val dialog = SelectPersonDialog()
            val args = Bundle()

            dialog.arguments = args
            return dialog
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (parentFragment is SelectPersonListener) {
            listener = parentFragment as SelectPersonListener
        }
    }

    override fun getLayoutId(): Int = R.layout.dialog_select_person

    override fun initViewListeners() {
        btn_cancel.setOnClickListener(this)
        til_search.editText?.addTextChangedListener(TextChangeListener {
            if (!it.toString().isEmpty()) {
                val list = ArrayList<Person>()
                adapter.items.filterTo(list) { person -> person.name.toLowerCase().contains(it.toString().toLowerCase()) }
                adapter.setFilteredItems(list)
            } else {
                adapter.setFilteredItems(adapter.items)
            }
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_items.layoutManager = LinearLayoutManager(activity)
        rv_items.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        rv_items.adapter = adapter
        disposable = loadPersons()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            adapter.setItems(it)
                            progress.visibility = View.GONE
                            root.visibility = View.VISIBLE
                        },
                        {
                            progress.visibility = View.GONE
                            root.visibility = View.VISIBLE
                        })
    }

    private fun loadPersons(): Single<List<Person>> {
        progress.visibility = View.VISIBLE
        root.visibility = View.INVISIBLE
        return Single.create({
            try {
                it.onSuccess(DataManager.readPersons(activity))
            } catch (ex: Exception) {
                it.onError(ex)
            }
        }
        )
    }

    private fun personSelected(person: Person, position: Int) {
        hideKeyboard()
        listener.onPersonSelected(person)
        this.dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cancel -> dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) disposable.dispose()
    }
}