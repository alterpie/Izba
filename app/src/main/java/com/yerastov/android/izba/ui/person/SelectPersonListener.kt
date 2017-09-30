package com.yerastov.android.izba.ui.person

import com.yerastov.android.izba.domain.Person

/**
 * Created by yerastov on 27/09/17.
 */
internal interface SelectPersonListener {

    fun onPersonSelected(person: Person)
}