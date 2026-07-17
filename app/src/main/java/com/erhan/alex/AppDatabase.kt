package com.erhan.alex

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File

@Database(entities = [Entry::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "Efe"
                ).allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            reconcileLegacyImages(appContext, db)
                        }
                    })
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        /**
         * Relinks pre-v2 image files (named IMG_<pic>.jpg) to the new IMG_<uuid>.jpg scheme.
         * Runs on open against committed uuids, so it's idempotent and interruption-safe:
         * if it's ever cut short, the next open finishes the remaining renames. Once every
         * legacy file has been relinked the work is skipped via a one-time flag.
         */
        private fun reconcileLegacyImages(context: Context, db: SupportSQLiteDatabase) {
            val prefs = context.getSharedPreferences("cloud_sync_prefs", Context.MODE_PRIVATE)
            if (prefs.getBoolean("legacy_images_relinked", false)) return

            val imagesDir = File(context.filesDir, "images")
            val cursor = db.query("SELECT pic, uuid FROM Entries")
            cursor.use {
                val picIndex = it.getColumnIndexOrThrow("pic")
                val uuidIndex = it.getColumnIndexOrThrow("uuid")
                while (it.moveToNext()) {
                    val pic = it.getInt(picIndex)
                    val uuid = it.getString(uuidIndex)
                    if (uuid.isNullOrEmpty()) continue
                    val target = File(imagesDir, "IMG_$uuid.jpg")
                    val legacy = File(imagesDir, "IMG_$pic.jpg")
                    if (!target.exists() && legacy.exists()) {
                        legacy.renameTo(target)
                    }
                }
            }
            prefs.edit().putBoolean("legacy_images_relinked", true).apply()
        }
    }
}
