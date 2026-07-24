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
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * A row in the list. The list is no longer 1:1 with the entries, so the display number is
     * resolved once in [setEntries] and carried here — deriving it from the adapter position at
     * bind time is what previously handed a day's highest number to its *oldest* entry.
     */
    private sealed interface Row {
        data class EntryRow(val entry: Entry, val number: Int) : Row
        data class Memorial(val firstLost: Int, val lastLost: Int) : Row
    }

    private var rows = emptyList<Row>()

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val numberView: TextView = itemView.findViewById(R.id.numberView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    class MemorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rangeView: TextView = itemView.findViewById(R.id.memorialRange)
    }

    override fun getItemViewType(position: Int): Int = when (rows[position]) {
        is Row.EntryRow -> TYPE_ENTRY
        is Row.Memorial -> TYPE_MEMORIAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_MEMORIAL) {
            MemorialViewHolder(inflater.inflate(R.layout.item_memorial, parent, false))
        } else {
            ListViewHolder(inflater.inflate(R.layout.item_card, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is Row.EntryRow -> bindEntry(holder as ListViewHolder, row)
            is Row.Memorial -> bindMemorial(holder as MemorialViewHolder, row)
        }
    }

    private fun bindEntry(holder: ListViewHolder, row: Row.EntryRow) {
        val currentItem = row.entry
        holder.imageView.setImageResource(R.drawable.placeholder);
        val file = File(context.filesDir?.path, "images").resolve("IMG_"+currentItem.uuid+".jpg")
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if ( bitmap != null ) {
            holder.imageView.setImageBitmap(bitmap)
        }
        else Log.i("info", "no image for ${currentItem.name}")
        holder.textView.text = currentItem.name
        val formattedOrder = "${row.number}. "
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
            bundle.putString("uuid", currentItem.uuid)
            val viewItemFragment = ViewItemFragment()
            viewItemFragment.arguments = bundle
            viewItemFragment.show((context as AppCompatActivity).supportFragmentManager, "ViewItemFragment")
        }
    }

    private fun bindMemorial(holder: MemorialViewHolder, row: Row.Memorial) {
        holder.rangeView.text =
            context.getString(R.string.memorialRange, row.firstLost, row.lastLost)
    }

    fun setEntries(newEntries: List<Entry>) {
        // Dates are day-granular, so same-day entries need an explicit tiebreak: without one the
        // stable sort leaves them oldest-first, and the numbering below then hands the day's
        // highest number to its oldest entry. id ascends with insertion, so descending id puts
        // the most recent entry of a day on top, where its number belongs.
        val sorted = newEntries.sortedWith(
            compareByDescending<Entry> { representDateSortably(it.date) }.thenByDescending { it.id }
        )

        val survivors = sorted.count { !isAfterLoss(it.date) }
        val memorial = Row.Memorial(survivors + 1, survivors + LOST_ENTRIES)

        val built = ArrayList<Row>(sorted.size + 1)
        var memorialPlaced = false
        sorted.forEachIndexed { index, entry ->
            // Newest-first, so the lost entries belong above every surviving one: drop the
            // memorial in just ahead of the first entry that predates the loss.
            if (!memorialPlaced && !isAfterLoss(entry.date)) {
                built.add(memorial)
                memorialPlaced = true
            }
            val number = (sorted.size - index) +
                    if (isAfterLoss(entry.date)) LOST_ENTRIES else 0
            built.add(Row.EntryRow(entry, number))
        }
        // Every entry postdates the loss, so the memorial is the oldest thing in the list.
        // Skipped entirely on an empty list — a lone memorial with nothing to mourn reads oddly.
        if (!memorialPlaced && sorted.isNotEmpty()) {
            built.add(memorial)
        }

        rows = built
        notifyDataSetChanged() // Use DiffUtil for better performance
    }

    override fun getItemCount() = rows.size

    private companion object {
        const val TYPE_ENTRY = 0
        const val TYPE_MEMORIAL = 1
    }
}
