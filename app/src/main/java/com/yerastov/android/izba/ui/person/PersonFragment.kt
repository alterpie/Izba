package com.yerastov.android.izba.ui.person

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.yerastov.android.izba.*
import com.yerastov.android.izba.data.PrefsManager
import com.yerastov.android.izba.domain.Language
import com.yerastov.android.izba.domain.Person
import com.yerastov.android.izba.ui.base.BaseFragment
import com.yerastov.android.izba.ui.supplier.SupplierFragment
import kotlinx.android.synthetic.main.fragment_person.*

/**
 * Created by yerastov on 22/09/17.
 */
class PersonFragment : BaseFragment<MainActivity>(), View.OnClickListener, AddPersonListener, SelectPersonListener {

    private val DLG_SELECT_PERSON: String = "dlg_select_person"
    private val DLG_ADD_PERSON: String = "dlg_add_person"

    private var person: Person? = null

    companion object {
        private val KEY_PERSON: String = "KEY_PERSON"

        fun newInstance(person: Person?): PersonFragment {
            val dialog = PersonFragment()
            val args = Bundle()
            args.putParcelable(KEY_PERSON, person)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        this.activity = activity as? MainActivity
    }

    override fun onPersonAdded(person: Person) {
        til_input_person.setText(person.name)
        til_device.setText(person.device)
        this.person = person
    }

    override fun onPersonSelected(person: Person) {
        til_input_person.setText(person.name)
        til_device.setText(person.device)
        this.person = person
    }

    private fun initViewListeners() {
        btn_next.setOnClickListener(this)
        btn_input_person.setOnClickListener(this)
        btn_add_person.setOnClickListener(this)
        btn_ru.setOnClickListener(this)
        btn_cn.setOnClickListener(this)
        btn_en.setOnClickListener(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_person

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_person -> {
                func = AddPersonDialog.newInstance().show(childFragmentManager, DLG_ADD_PERSON)
                checkStoragePermissions {
                    func
                }
            }
            R.id.btn_input_person -> {
                func = SelectPersonDialog.newInstance().show(childFragmentManager, DLG_SELECT_PERSON)
                checkStoragePermissions {
                    func
                }
            }
            R.id.btn_next -> person?.let { activity?.replaceFragment(SupplierFragment.newInstance(it, null)) }

            R.id.btn_cn -> {
                PrefsManager.setLocale(Language.CHINESE, getActivity())
                getActivity().changeLanguage()
                getActivity().recreate()
            }
            R.id.btn_ru -> {
                PrefsManager.setLocale(Language.RUSSIAN, getActivity())
                getActivity().changeLanguage()
                getActivity().recreate()
            }
            R.id.btn_en -> {
                PrefsManager.setLocale(Language.ENGLISH, getActivity())
                getActivity().changeLanguage()
                getActivity().recreate()
            }

        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val person: Person? = arguments.getParcelable(KEY_PERSON)
        person?.let {
            this.person = person
            til_input_person.setText(person.name)
            til_device.setText(person.device)
        }
        PrefsManager.getDeviceId(getActivity()).let {
            if (!it.isEmpty()) til_device.setText(it)
        }
        Glide.with(this).asGif().load(R.raw.panda).into(iv_panda)
        initViewListeners()
    }

    lateinit var func: Unit

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        checkStoragePermissions {
            func
        }
    }

}