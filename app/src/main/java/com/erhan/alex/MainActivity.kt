package com.erhan.alex

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
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

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        dario = BitmapFactory.decodeResource(resources, R.drawable.dario)

        val db by lazy { AppDatabase.getDatabase(this) }
        val dao = db.entryDao()

        val howMany = "You've had "+dao.getCount().toString()+" brewskis."
        val howManyView: TextView = findViewById(R.id.howMany)
        howManyView.text = howMany


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
            addItemFragment.onItemAdded = { nameT, whereT, kindT, rateT, noteT ->
                addItem(dao, nameT, whereT, kindT, rateT, noteT)
            }
            addItemFragment.show(supportFragmentManager, "AddItemFragment")
        }
    }

    private fun addItem(dao: EntryDao, name: String, where: String, kind: String, rating: Int, notes: String) {
        val maxPic = dao.getMaxPic()
        val newPic: Int = if ( dao.getCount() == 0 ) {
            0
        } else {
            maxPic+1
        }
        entryViewModel.insert(Entry(
            name = name,
            bwhere = where,
            kind = kind,
            date = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Calendar.getInstance().time),
            rating = rating,
            notes = notes,
            pic = newPic))
        Log.i("entryadded","Added entry $name $rating $notes $newPic")
        val howMany = "You've had "+dao.getCount().toString()+" unique brewskis."
        val howManyView: TextView = findViewById(R.id.howMany)
        howManyView.text = howMany
    }
}