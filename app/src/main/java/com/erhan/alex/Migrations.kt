package com.erhan.alex

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

/**
 * v1 -> v2: adds the `uuid` (stable cloud sync key) and `updatedAt` (last-write-wins)
 * columns and backfills them onto every existing row. Purely additive and fully
 * transactional — existing entries keep all their data; nothing is dropped or recreated.
 *
 * Image files are intentionally NOT renamed here: file I/O can't participate in the SQLite
 * transaction, so a rename done here could be orphaned if the migration rolls back and
 * re-runs with different UUIDs. The uuid<->image relink happens in an idempotent pass after
 * open instead (AppDatabase.reconcileLegacyImages), which runs against committed uuids.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Entries ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE Entries ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

        val now = System.currentTimeMillis()
        val cursor = db.query("SELECT id FROM Entries")
        cursor.use {
            val idIndex = it.getColumnIndexOrThrow("id")
            while (it.moveToNext()) {
                val id = it.getInt(idIndex)
                db.execSQL(
                    "UPDATE Entries SET uuid = ?, updatedAt = ? WHERE id = ?",
                    arrayOf(UUID.randomUUID().toString(), now, id)
                )
            }
        }

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_Entries_uuid ON Entries(uuid)")
    }
}
