package com.erhan.alex

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


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
//      SET IMAGE HERE!
        holder.imageView.setImageResource(R.drawable.dario)
        holder.textView.text = currentItem.name

        holder.itemView.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("name", currentItem.name)
            bundle.putString("date", currentItem.date)
            bundle.putString("rating", currentItem.rating.toString())
            bundle.putString("notes", currentItem.notes)
            val viewItemFragment = ViewItemFragment()
            viewItemFragment.arguments = bundle
            viewItemFragment.show((context as AppCompatActivity).supportFragmentManager, "ViewItemFragment")
        }
    }

    fun setEntries(newEntries: List<Entry>) {
        entries = newEntries
        notifyDataSetChanged() // Use DiffUtil for better performance
    }

    override fun getItemCount() = entries.size
}