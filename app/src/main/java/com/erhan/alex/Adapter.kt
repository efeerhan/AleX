package com.erhan.alex

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class Adapter( private val context: Context) :
    RecyclerView.Adapter<Adapter.ListViewHolder>() {

    private var entries = emptyList<Entry>()

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = entries[position]
        val file = File(context.filesDir?.path, "images").resolve("IMG_"+currentItem.pic+".jpg")

        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
        holder.textView.text = currentItem.name
        holder.textView.textSize = 24f

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", currentItem.id)
            bundle.putString("name", currentItem.name)
            bundle.putString("where", currentItem.bwhere)
            bundle.putString("kind", currentItem.kind)
            bundle.putString("date", currentItem.date)
            bundle.putString("notes", currentItem.notes)
            bundle.putInt("pic", currentItem.pic)
            val viewItemFragment = ViewItemFragment()
            viewItemFragment.arguments = bundle
            viewItemFragment.show((context as AppCompatActivity).supportFragmentManager, "ViewItemFragment")
        }
    }

    fun setEntries(newEntries: List<Entry>) {
        entries = newEntries.sortedBy { it.date }
        notifyDataSetChanged() // Use DiffUtil for better performance
    }

    override fun getItemCount() = entries.size
}