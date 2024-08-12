package com.erhan.alex

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: Adapter
    private lateinit var itemList: MutableList<entry>
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        itemList = mutableListOf()
        itemList.add(entry("item1", Calendar.getInstance().time, 5, "yeah1",  R.drawable.dario))
        itemList.add(entry("item2", Calendar.getInstance().time, 1, "yeah2",  R.drawable.dario))
        // Add more items as needed

        recyclerView = findViewById(R.id.recycle)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listAdapter = Adapter(itemList, this)
        recyclerView.adapter = listAdapter

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val addItemFragment = AddItemFragment()
            addItemFragment.onItemAdded = { newItemText ->
                addItem(newItemText, R.drawable.dario)
            }
            addItemFragment.show(supportFragmentManager, "AddItemFragment")
        }
    }

    private fun addItem(text: String, imageResId: Int) {
        itemList.add(entry(text, Calendar.getInstance().time, 0, "yeah1",  R.drawable.dario))
        listAdapter.notifyItemInserted(itemList.size - 1)
        recyclerView.scrollToPosition(itemList.size - 1)
    }
}