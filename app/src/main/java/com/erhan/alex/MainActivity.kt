package com.erhan.alex

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hello: TextView
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

        val howMany = "You've logged "+dao.getCount().toString()+" brewskis."
        val howManyView: TextView = findViewById(R.id.howMany)
        howManyView.text = howMany

        hello = findViewById(R.id.hello)
        val names: Array<String> = arrayOf(
            "Alex",
            "Alexandra",
            "darling",
            "sweetheart",
            "cutie",
            "my love",
            "sweet cheeks",
            "pretty girl",
            "beautiful",
            "hot stuff",
            "my Bort",
            "the Alexsterrr",
            "\uD83C\uDF51"
        )
        val randInt = (0..names.size-1).random()
        val welcome = "Hey, "+names[randInt]+"!"
        hello.text = welcome

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
            addItemFragment.onItemAdded = { nameT, whereT, kindT, dateT, noteT ->
                addItem(dao, nameT, whereT, kindT, dateT, noteT)
            }
            addItemFragment.show(supportFragmentManager, "AddItemFragment")
        }
    }

    private fun addItem(dao: EntryDao, name: String, where: String, kind: String, date: String, notes: String) {
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
            date = date,
            notes = notes,
            pic = newPic))
        Log.i("entryadded","Added entry $name $date $notes $newPic")
        val howMany = "You've logged "+dao.getCount().toString()+" brewskis."
        val howManyView: TextView = findViewById(R.id.howMany)
        howManyView.text = howMany
    }
}