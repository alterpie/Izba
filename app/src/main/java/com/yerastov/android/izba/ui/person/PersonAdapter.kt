package com.yerastov.android.izba.ui.person

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yerastov.android.izba.R
import com.yerastov.android.izba.domain.Person
import kotlinx.android.synthetic.main.list_item_person.view.*

/**
 * Created by yerastov on 28/09/17.
 */
internal class PersonAdapter(private val itemClickListener: (person: Person, position: Int) -> Unit) : RecyclerView.Adapter<PersonAdapter.PersonHolder>() {

    internal val items = ArrayList<Person>()
    internal val filteredItems = ArrayList<Person>()
    override fun onBindViewHolder(holder: PersonHolder?, position: Int) {
        holder?.bind(filteredItems[position], itemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PersonHolder =
            PersonHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_person, parent, false))

    override fun getItemCount(): Int = filteredItems.size

    inner class PersonHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(person: Person, listener: (person: Person, position: Int) -> Unit) {
            itemView.tv_name.text = person.name
            itemView.tv_device.text = person.device
            itemView.root.setOnClickListener({
                if (adapterPosition != RecyclerView.NO_POSITION) listener(filteredItems[adapterPosition], adapterPosition)
                else return@setOnClickListener
            })
        }
    }

    fun setItems(newItems: List<Person>) {
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

    fun setFilteredItems(newItems: List<Person>) {
        this.filteredItems.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }
}