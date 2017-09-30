package com.yerastov.android.izba.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.yerastov.android.izba.R
import kotlinx.android.synthetic.main.item_photo.view.*

/**
 * Created by yerastov on 29/09/17.
 */
class PhotoAdapter(private val deleteClickListener: (path: String, position: Int) -> Unit) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    val items = ArrayList<String>()

    override fun onBindViewHolder(holder: PhotoHolder?, position: Int) {
        holder?.bind(items[position], deleteClickListener)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PhotoHolder =
            PhotoHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_photo, parent, false))

    fun addItem(path: String) {
        items.add(path)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(path: String, position: Int) {
        items.remove(path)
        notifyItemRemoved(position)
    }

    fun setItems(newItems: List<String>) {
        with(items) {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }

    inner class PhotoHolder(val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(photoUri: String, listener: (path: String, position: Int) -> Unit) {
            Glide.with(v).load(photoUri).into(v.iv_photo)
            v.iv_delete_photo.setOnClickListener({
                if (adapterPosition != RecyclerView.NO_POSITION) listener(items[adapterPosition], adapterPosition)
            })
        }
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }
}