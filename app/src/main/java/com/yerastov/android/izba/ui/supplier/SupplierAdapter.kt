package com.yerastov.android.izba.ui.supplier

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yerastov.android.izba.R
import com.yerastov.android.izba.domain.Supplier
import kotlinx.android.synthetic.main.list_item_supplier.view.*
import java.util.*

/**
 * Created by yerastov on 28/09/17.
 */
internal class SupplierAdapter(private val itemClickListener: (supplier: Supplier, position: Int) -> Unit) : RecyclerView.Adapter<SupplierAdapter.SupplierHolder>() {

    internal val items = ArrayList<Supplier>()
    internal val filteredItems = ArrayList<Supplier>()

    override fun onBindViewHolder(holder: SupplierHolder?, position: Int) {
        holder?.bind(filteredItems[position], itemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SupplierHolder =
            SupplierHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_supplier, parent, false))

    override fun getItemCount(): Int = filteredItems.size

    inner class SupplierHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(supplier: Supplier, listener: (supplier: Supplier, position: Int) -> Unit) {
            itemView.tv_supplier.text = supplier.name
            itemView.root.setOnClickListener({
                if (adapterPosition != RecyclerView.NO_POSITION) listener(filteredItems[adapterPosition], adapterPosition)
                else return@setOnClickListener
            })
        }
    }

    fun setItems(newItems: List<Supplier>) {
        this.items.apply {
            clear()
            addAll(newItems)
        }
        this.filteredItems.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }

    fun setFilteredItems(newItems: List<Supplier>) {
        this.filteredItems.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }
}