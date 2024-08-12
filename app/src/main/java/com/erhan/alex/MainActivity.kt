package com.erhan.alex

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var entryViewModel: EntryViewModel
    private lateinit var entryAdapter: Adapter
    private lateinit var fab: FloatingActionButton
    private lateinit var dario: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        dario = BitmapFactory.decodeResource(resources, R.drawable.dario)

        val db by lazy { AppDatabase.getDatabase(this) }
        val dao = db.entryDao()

        entryAdapter = Adapter(this)

        recyclerView = findViewById(R.id.recycle)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = entryAdapter

        entryViewModel = ViewModelProvider(this)[EntryViewModel::class.java]

        entryViewModel.allEntries.observe(this) { entries ->
            entries?.let { entryAdapter.setEntries(it) }
        }

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val addItemFragment = AddItemFragment()
            addItemFragment.onItemAdded = { nameT, rateT, noteT ->
                addItem(dao, nameT, rateT, noteT)
            }
            addItemFragment.show(supportFragmentManager, "AddItemFragment")
        }
    }

    private fun addItem(dao: EntryDao, name: String, rating: Int, notes: String) {
        entryViewModel.insert(Entry(name, SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Calendar.getInstance().time), rating, notes, dao.getMaxPic()+1))
    }
}