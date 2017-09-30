package com.yerastov.android.izba.ui.person

import com.yerastov.android.izba.domain.Person

/**
 * Created by yerastov on 26/09/17.
 */
internal interface AddPersonListener {

    fun onPersonAdded(person: Person)
}