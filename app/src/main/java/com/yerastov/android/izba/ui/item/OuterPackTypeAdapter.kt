package com.yerastov.android.izba.ui.item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yerastov.android.izba.domain.OuterPackType

/**
 * Created by yerastov on 29/09/17.
 */
internal class OuterPackTypeAdapter(context: Context) : BaseAdapter() {
    private val items = OuterPackType.createAvailableList(context)

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View? {
        var v = view
        val holder: Holder
        if (v == null) {
            v = LayoutInflater.from(viewGroup?.context).inflate(android.R.layout.simple_spinner_item, viewGroup, false)
            holder = Holder(v)
            v.tag = holder
        } else {
            holder = v.tag as Holder
        }
        holder.bind(items[i])
        return v
    }

    override fun getItem(position: Int): OuterPackType = items[position]

    override fun getItemId(p0: Int): Long = 0

    override fun getCount(): Int = items.size

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return getView(position, convertView, parent)
    }

    private inner class Holder(view: View) {
        val tvName: TextView = view.findViewById(android.R.id.text1)

        fun bind(outerPackType: OuterPackType) {
            tvName.text = outerPackType.name
        }
    }
}