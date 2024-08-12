package com.erhan.alex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val itemList: List<entry>, private val context: Context) :
    RecyclerView.Adapter<Adapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = itemList[position]
//        holder.imageView.setImageResource(currentItem.pic)
        holder.imageView.setImageResource(R.drawable.dario)
        holder.textView.text = currentItem.name

        holder.itemView.setOnClickListener {
            val text = "ballsack!"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, text, duration) // in Activity
            toast.show()
        }
    }

    override fun getItemCount() = itemList.size
}