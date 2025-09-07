package com.erhan.alex

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
        val numberView: TextView = itemView.findViewById(R.id.numberView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = entries[position]
        val file = File(context.filesDir?.path, "images").resolve("IMG_"+currentItem.pic+".jpg")
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if ( bitmap != null ) {
            holder.imageView.setImageBitmap(bitmap)
        }
        holder.textView.text = currentItem.name
        val formattedOrder = "${(position+1).toString()}. "
        holder.numberView.text = formattedOrder

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

    private fun representDateSortably(date: String): String{
        // OG is month day year
        // 12/31/1970
        // 0123456789
        val month = date.substring(0, 2)
        val day = date.substring(3,5)
        val year = date.substring(6)
        val reformat = "$year/$month/$day"
        return reformat
    }

    fun setEntries(newEntries: List<Entry>) {
        entries = newEntries.sortedBy { representDateSortably(it.date) }
        notifyDataSetChanged() // Use DiffUtil for better performance
    }

    override fun getItemCount() = entries.size
}