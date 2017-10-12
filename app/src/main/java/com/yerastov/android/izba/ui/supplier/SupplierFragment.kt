package com.yerastov.android.izba.ui.supplier

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.yerastov.android.izba.MainActivity
import com.yerastov.android.izba.R
import com.yerastov.android.izba.data.DataManager
import com.yerastov.android.izba.domain.Person
import com.yerastov.android.izba.domain.Supplier
import com.yerastov.android.izba.setText
import com.yerastov.android.izba.toast
import com.yerastov.android.izba.ui.PhotoAdapter
import com.yerastov.android.izba.ui.base.BaseFragment
import com.yerastov.android.izba.ui.item.ItemFragment
import com.yerastov.android.izba.ui.person.PersonFragment
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_supplier.*
import kotlinx.android.synthetic.main.progress.*

/**
 * Created by yerastov on 22/09/17.
 */
class SupplierFragment : BaseFragment<MainActivity>(), SelectSupplierListener, AddSupplierListener, View.OnClickListener {

    private var supplier: Supplier? = null
    private val adapter = PhotoAdapter(this::onDeletePhotoClicked)
    private var deleteDisposable: Disposable? = null
    private var saveDisposable: Disposable? = null
    private var loadDisposable: Disposable? = null

    override fun onSupplierAdded(supplier: Supplier) {
        this.supplier = supplier
        til_supplier_name.setText(supplier.name)
        til_supplier_id.setText(supplier.id)
        adapter.clearItems()
        iv_main_photo.setImageDrawable(null)
    }

    override fun onSupplierSelected(supplier: Supplier) {
        this.supplier = supplier
        iv_main_photo.setImageDrawable(null)
        til_supplier_name.setText(supplier.name)
        til_supplier_id.setText(supplier.id)
        loadSupplierPhotos(supplier)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        this.activity = activity as? MainActivity
    }

    companion object {
        private val KEY_PERSON: String = "KEY_PERSON"
        private val KEY_SUPPLIER: String = "KEY_SUPPLIER"
        private val DLG_ADD_SUPPLIER = "DLG_ADD_SUPPLIER"
        private val DLG_SELECT_SUPPLIER = "DLG_SELECT_SUPPLIER"
        fun newInstance(person: Person, supplier: Supplier?): SupplierFragment {
            val dialog = SupplierFragment()
            val args = Bundle()
            args.putParcelable(KEY_PERSON, person)
            args.putParcelable(KEY_SUPPLIER, supplier)
            dialog.arguments = args
            return dialog
        }
    }

    private fun initViewListeners() {
        btn_add_supplier.setOnClickListener(this)
        btn_select_supplier.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        iv_add_photo.setOnClickListener(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_supplier

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewListeners()
        rv_items.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rv_items.adapter = adapter

        arguments.getParcelable<Supplier>(KEY_SUPPLIER)?.let {
            this.supplier = it
            til_supplier_id.setText(it.id)
            til_supplier_name.setText(it.name)
            loadSupplierPhotos(it)
        }
    }

    private fun onDeletePhotoClicked(path: String, position: Int) {
        AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_photo_question))
                .setPositiveButton(getString(R.string.yes), { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    supplier?.let {
                        deleteDisposable = deletePhoto(path, it)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            progress.visibility = View.GONE
                                            if (position == 0) {
                                                if (adapter.items.size > 1) {
                                                    Glide.with(this)
                                                            .load(adapter.items[position + 1])
                                                            .into(iv_main_photo)
                                                } else {
                                                    iv_main_photo.setImageDrawable(null)
                                                }
                                            }
                                            Glide.get(getActivity()).clearMemory()
                                            adapter.removeItem(path, position)
                                        },
                                        {
                                            progress.visibility = View.GONE
                                            activity?.toast(it.message)
                                        })
                    }
                })
                .setNegativeButton(getString(R.string.no), { dialogInterface, _ -> dialogInterface.dismiss() })
                .show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_supplier -> AddSupplierDialog.newInstance(arguments.getParcelable(KEY_PERSON)).show(childFragmentManager, DLG_ADD_SUPPLIER)

            R.id.btn_select_supplier -> SelectSupplierDialog.newInstance().show(childFragmentManager, DLG_SELECT_SUPPLIER)

            R.id.btn_next -> supplier?.let { activity?.replaceFragment(ItemFragment.newInstance(arguments.getParcelable(KEY_PERSON), it)) }

            R.id.btn_back -> activity?.replaceFragment(PersonFragment.newInstance(arguments.getParcelable(KEY_PERSON)))

            R.id.iv_add_photo -> {
                if (supplier != null) {
                    if (adapter.items.size < 10) {
                        FilePickerBuilder.getInstance()
                                .setMaxCount(1)
                                .setActivityTheme(R.style.AppTheme)
                                .enableCameraSupport(true)
                                .pickPhoto(this)
                    } else {
                        activity?.toast(getString(R.string.template_maximum_files_allowed, 10))
                    }
                } else {
                    activity?.toast(getString(R.string.choose_supplier))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && data != null) {
                val photoPath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                if (photoPath.size > 0) {
                    supplier?.let {
                        saveDisposable = savePhoto(photoPath[0], it)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            progress.visibility = View.GONE
                                            if (adapter.items.size == 0) {
                                                Glide.with(this)
                                                        .load(it)
                                                        .into(iv_main_photo)
                                                Glide.get(getActivity()).clearMemory()
                                            }
                                            adapter.addItem(it)
                                        },
                                        {
                                            progress.visibility = View.GONE
                                            activity?.toast(it.message)
                                        })
                    }
                }
            }
        }
    }

    private fun loadSupplierPhotos(supplier: Supplier) {
        loadDisposable = loadPhotos(supplier)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.isNotEmpty()) {
                                Glide.with(this).load(it[0]).into(iv_main_photo)
                            }
                            adapter.setItems(it)
                            progress.visibility = View.GONE
                        },
                        {
                            progress.visibility = View.GONE
                            activity?.toast(it.message)
                        })
    }

    private fun savePhoto(path: String, supplier: Supplier): Single<String> {
        progress.visibility = View.VISIBLE
        return Single.create({ e ->
            try {
                e.onSuccess(DataManager.saveSupplierPhoto(path, supplier.name, supplier.id, getActivity()))
            } catch (ex: Exception) {
                e.onError(ex)
            }
        })
    }

    private fun deletePhoto(path: String, supplier: Supplier): Completable {
        progress.visibility = View.VISIBLE
        return Completable.create({ e ->
            try {
                DataManager.deletePhoto(path, getActivity())
                e.onComplete()
            } catch (ex: Exception) {
                e.onError(ex)
            }
        })
    }

    private fun loadPhotos(supplier: Supplier): Single<List<String>> {
        progress.visibility = View.VISIBLE
        return Single.create({ e ->
            try {
                e.onSuccess(DataManager.loadSupplierPhotos(supplier.id, getActivity()))
            } catch (ex: Exception) {
                e.onError(ex)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        loadDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        deleteDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }
}