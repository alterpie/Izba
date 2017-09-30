package com.yerastov.android.izba.ui.item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.yerastov.android.izba.*
import com.yerastov.android.izba.data.DataManager
import com.yerastov.android.izba.data.PrefsManager
import com.yerastov.android.izba.domain.*
import com.yerastov.android.izba.ui.PhotoAdapter
import com.yerastov.android.izba.ui.base.BaseFragment
import com.yerastov.android.izba.ui.supplier.SupplierFragment
import com.yerastov.android.izba.utils.LimitDecimalTextChangeListener
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.android.synthetic.main.progress.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yerastov on 22/09/17.
 */
class ItemFragment : BaseFragment<MainActivity>(), View.OnClickListener {

    private lateinit var supplier: Supplier
    private lateinit var person: Person
    private val adapter = PhotoAdapter(this::onDeletePhotoClicked)
    private var mainPhotoPath: String = ""
    private var deleteDisposable: Disposable? = null
    private var saveDisposable: Disposable? = null

    companion object {
        private val KEY_PERSON: String = "KEY_PERSON"
        private val KEY_SUPPLIER: String = "KEY_SUPPLIER"

        fun newInstance(person: Person, supplier: Supplier): ItemFragment {
            val dialog = ItemFragment()
            val args = Bundle()
            with(args) {
                putParcelable(KEY_PERSON, person)
                putParcelable(KEY_SUPPLIER, supplier)
            }

            dialog.arguments = args
            return dialog
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        this.activity = activity as? MainActivity
    }

    private fun initViewListeners() {
        btn_back.setOnClickListener(this)
        btn_save_item_create_new.setOnClickListener(this)
        btn_save_item_exit.setOnClickListener(this)
        btn_add_photo.setOnClickListener(this)
        til_outer_pack_weight_g_w.editText?.addTextChangedListener(LimitDecimalTextChangeListener(3, til_outer_pack_weight_g_w))

        til_outer_pack_weight_n_w.editText?.addTextChangedListener(LimitDecimalTextChangeListener(3, til_outer_pack_weight_n_w))

        til_unit_n_w.editText?.addTextChangedListener(LimitDecimalTextChangeListener(3, til_unit_n_w))

        til_outer_pack_height.editText?.addTextChangedListener(LimitDecimalTextChangeListener(2, til_outer_pack_height))

        til_outer_pack_length.editText?.addTextChangedListener(LimitDecimalTextChangeListener(2, til_outer_pack_length))

        til_outer_pack_volume.editText?.addTextChangedListener(LimitDecimalTextChangeListener(4, til_outer_pack_volume))

        til_price.editText?.addTextChangedListener(LimitDecimalTextChangeListener(4, til_price))

        til_outer_pack_width.editText?.addTextChangedListener(LimitDecimalTextChangeListener(2, til_outer_pack_width))
    }

    override fun getLayoutId(): Int = R.layout.fragment_item

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewListeners()
        rv_items.layoutManager = LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        rv_items.adapter = adapter
        supplier = arguments.getParcelable(KEY_SUPPLIER)
        person = arguments.getParcelable(KEY_PERSON)
        tv_date_time.text = SimpleDateFormat("dd.MM.yy hh-mm", Locale.getDefault()).format(Date())
        tv_supplier.text = supplier.name
        tv_supplier_id.text = supplier.id
        tv_item_id.text = supplier.id + '_' + PrefsManager.generateItemId(getActivity())
        spinner_inner_pack_type.adapter = InnerPackTypeAdapter(getActivity())
        spinner_inner_pack_type.setSelection(0)
        spinner_outer_pack_type.adapter = OuterPackTypeAdapter(getActivity())
        spinner_outer_pack_type.setSelection(0)
        spinner_outer_pack_type.adapter = OuterPackTypeAdapter(getActivity())
        spinner_outer_pack_type.setSelection(0)
        spinner_moq_unit.adapter = MoqUnitAdapter(getActivity())
        spinner_moq_unit.setSelection(0)
        spinner_price_basis.adapter = PriceBasisAdapter(getActivity())
        spinner_price_basis.setSelection(0)
        spinner_product_unit.adapter = ProductUnitAdapter(getActivity())
        spinner_product_unit.setSelection(0)
        spinner_unit_packing_details.adapter = UnitPackingAdapter(getActivity())
        spinner_unit_packing_details.setSelection(0)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> activity?.replaceFragment(SupplierFragment.newInstance(person, supplier))
            R.id.btn_save_item_create_new -> {
                saveItem()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    progress.visibility = View.GONE
                                    adapter.clearItems()
                                    tv_item_id.text = supplier.id + '_' + PrefsManager.generateItemId(getActivity())
                                },
                                {
                                    activity?.toast(it.message)
                                })
            }
            R.id.btn_save_item_exit -> {
                saveItem()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    activity?.replaceFragment(SupplierFragment.newInstance(person, supplier))
                                },
                                {
                                    progress.visibility = View.GONE
                                    activity?.toast(it.message)
                                })
            }

            R.id.btn_add_photo -> {
                if (adapter.items.size < 20) {
                    FilePickerBuilder.getInstance()
                            .setMaxCount(1)
                            .setActivityTheme(R.style.AppTheme)
                            .enableCameraSupport(true)
                            .pickPhoto(this)
                } else {
                    activity?.toast(getString(R.string.template_maximum_files_allowed, 20))
                }
            }
        }
    }

    private fun onDeletePhotoClicked(path: String, position: Int) {
        AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_photo_question))
                .setPositiveButton(getString(R.string.yes), { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    deleteDisposable = deletePhoto(path)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        progress.visibility = View.GONE
                                        if (position == 0) {
                                            mainPhotoPath = if (adapter.items.size == 0) ""
                                            else adapter.items[1]
                                        }
                                        Glide.get(getActivity()).clearMemory()
                                        adapter.removeItem(path, position)
                                    },
                                    {
                                        progress.visibility = View.GONE
                                        activity?.toast(it.message)
                                    })
                })
                .setNegativeButton(getString(R.string.no), { dialogInterface, _ -> dialogInterface.dismiss() })
                .show()
    }

    private fun deletePhoto(path: String): Completable {
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

    private fun savePhoto(path: String): Single<String> {
        progress.visibility = View.VISIBLE
        return Single.create({ e ->
            try {
                e.onSuccess(DataManager.saveItemPhoto(path, tv_item_id.getString(), getActivity()))
            } catch (ex: Exception) {
                e.onError(ex)
            }
        })
    }

    private fun saveItem(): Completable {
        hideKeyboard()
        progress.visibility = View.VISIBLE
        return Completable.create({
            try {
                DataManager.saveItem(Item(tv_item_id.getString(),
                        supplier.id,
                        til_moq.getText(),
                        til_suppliers_item_number.getText(),
                        (spinner_price_basis.selectedItem as PriceBasis).name,
                        til_outer_pack_weight_g_w.getText(),
                        til_outer_pack_weight_n_w.getText(),
                        til_unit_n_w.getText(),
                        (spinner_outer_pack_type.selectedItem as OuterPackType).name,
                        (spinner_inner_pack_type.selectedItem as InnerPackType).name,
                        til_outer_pack_height.getText(),
                        tv_date_time.getString(),
                        til_outer_pack_length.getText(),
                        (spinner_moq_unit.selectedItem as MoqUnit).name,
                        (spinner_product_unit.selectedItem as ProductUnit).name,
                        til_qty_per_outer_pack.getText(),
                        til_qty_per_inner_pack.getText(),
                        til_comments.getText(),
                        til_price_comment.getText(),
                        til_material.getText(),
                        til_product_name.getText(),
                        til_outer_pack_volume.getText(),
                        til_set_details.getText(),
                        til_product_description.getText(),
                        person.name, tv_supplier.getString(),
                        til_size.getText(),
                        til_moq_lead_time.getText(),
                        (spinner_unit_packing_details.selectedItem as UnitPacking).name,
                        mainPhotoPath,
                        til_price.getText(),
                        til_outer_pack_width.getText()),
                        getActivity())
                it.onComplete()
            } catch (ex: Exception) {
                it.onError(ex)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && data != null) {
                val photoPath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                if (photoPath.size > 0) {
                    saveDisposable = savePhoto(photoPath[0])
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        progress.visibility = View.GONE
                                        Glide.get(getActivity()).clearMemory()
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

    override fun onDestroy() {
        super.onDestroy()
        saveDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        deleteDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }
}