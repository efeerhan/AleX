package com.erhan.alex

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hello: TextView
    private lateinit var entryViewModel: EntryViewModel
    private lateinit var entryAdapter: Adapter
    private lateinit var fab: FloatingActionButton
    private lateinit var dario: Bitmap
    private lateinit var syncStatus: TextView
    private var authListener: FirebaseAuth.AuthStateListener? = null

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

        val howMany = "You've logged "+(dao.getCount() + LOST_ENTRIES).toString()+" brewskis."
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

        syncStatus = findViewById(R.id.syncStatus)
        entryViewModel.pendingPushCount.observe(this) { updateSyncStatus() }

        // Bootstraps a session that was restored rather than freshly tapped, then reconciles.
        // No-ops when signed out, so the signed-out check that used to live here is gone.
        entryViewModel.onAppStart()

        val accountButton = findViewById<android.widget.ImageButton>(R.id.accountButton)
        accountButton.setOnClickListener {
            AccountFragment().show(supportFragmentManager, "AccountFragment")
        }

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val addItemFragment = AddItemFragment()
            addItemFragment.onItemAdded = { nameT, whereT, kindT, dateT, noteT, uuidT ->
                addItem(dao, nameT, whereT, kindT, dateT, noteT, uuidT)
            }
            addItemFragment.show(supportFragmentManager, "AddItemFragment")
        }
    }

    override fun onStart() {
        super.onStart()
        // A sign-out, or a token quietly expiring, has to visibly change the indicator —
        // that invisibility is what let a week of entries go un-backed-up unnoticed.
        authListener = AuthRepository.observeAuthState { updateSyncStatus() }
    }

    override fun onStop() {
        super.onStop()
        authListener?.let { AuthRepository.stopObservingAuthState(it) }
        authListener = null
    }

    private fun updateSyncStatus() {
        val pending = entryViewModel.pendingPushCount.value ?: 0
        val signedIn = AuthRepository.currentUser != null
        syncStatus.text = when {
            !signedIn -> getString(R.string.syncSignedOut)
            pending > 0 -> resources.getQuantityString(R.plurals.syncPending, pending, pending)
            else -> getString(R.string.syncUpToDate)
        }
        val healthy = signedIn && pending == 0
        syncStatus.setTextColor(
            ContextCompat.getColor(this, if (healthy) R.color.white else R.color.accent)
        )
    }

    private fun addItem(dao: EntryDao, name: String, where: String, kind: String, date: String, notes: String, uuid: String) {
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
            pic = newPic,
            uuid = uuid,
            updatedAt = System.currentTimeMillis()))
        Log.i("entryadded","Added entry $name $date $notes $newPic")
        val howMany = "You've logged "+(dao.getCount() + LOST_ENTRIES).toString()+" brewskis."
        val howManyView: TextView = findViewById(R.id.howMany)
        howManyView.text = howMany
    }
}