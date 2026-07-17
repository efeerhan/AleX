package com.erhan.alex

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * Owns all Firestore + Cloud Storage access, scoped to the signed-in user's uid.
 * Every method no-ops when signed out, so signed-out behavior is identical to the
 * pre-Firebase app. Writes are fire-and-forget (own IO scope, retried by the
 * reconcile pass at app start); reads/restore/reconcile are suspend fns awaited by callers.
 */
object CloudSyncRepository {

    private const val TAG = "CloudSync"

    // Process-lifetime scope: not tied to any ViewModel, so a fire-and-forget push
    // survives the user backing out of a screen immediately after a write.
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun uid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private fun entriesCollection(uid: String) =
        FirebaseFirestore.getInstance().collection("users").document(uid).collection("entries")

    private fun imageRef(uid: String, uuid: String) =
        FirebaseStorage.getInstance().reference.child("users/$uid/images/$uuid.jpg")

    private fun Entry.toFirestoreMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "bwhere" to bwhere,
        "kind" to kind,
        "date" to date,
        "notes" to notes,
        "updatedAt" to updatedAt
    )

    // ---- Fire-and-forget writes ----

    fun pushEntry(entry: Entry) {
        val uid = uid() ?: return
        ioScope.launch {
            try {
                entriesCollection(uid).document(entry.uuid).set(entry.toFirestoreMap()).await()
            } catch (e: Exception) {
                Log.w(TAG, "pushEntry failed for ${entry.uuid}", e)
            }
        }
    }

    fun pushImage(uuid: String, file: File) {
        val uid = uid() ?: return
        if (!file.exists()) return
        ioScope.launch {
            try {
                imageRef(uid, uuid).putFile(Uri.fromFile(file)).await()
            } catch (e: Exception) {
                Log.w(TAG, "pushImage failed for $uuid", e)
            }
        }
    }

    fun deleteEntry(uuid: String) {
        val uid = uid() ?: return
        ioScope.launch {
            try {
                entriesCollection(uid).document(uuid).delete().await()
            } catch (e: Exception) {
                Log.w(TAG, "deleteEntry (doc) failed for $uuid", e)
            }
            try {
                imageRef(uid, uuid).delete().await()
            } catch (e: StorageException) {
                // Object-not-found is expected when the entry never had a photo.
                if (e.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Log.w(TAG, "deleteEntry (image) failed for $uuid", e)
                }
            } catch (e: Exception) {
                Log.w(TAG, "deleteEntry (image) failed for $uuid", e)
            }
        }
    }

    // ---- Awaited reads (restore / reconcile) ----

    /** Returns (uuid, fields) for every remote entry doc, or empty if signed out. */
    suspend fun fetchAllRemoteEntries(): List<Pair<String, Map<String, Any?>>> {
        val uid = uid() ?: return emptyList()
        return try {
            entriesCollection(uid).get().await().documents.map { doc ->
                doc.id to doc.data.orEmpty()
            }
        } catch (e: Exception) {
            Log.w(TAG, "fetchAllRemoteEntries failed", e)
            emptyList()
        }
    }

    /** Downloads the remote image for [uuid] into [destFile]; no-ops if it doesn't exist. */
    suspend fun downloadImage(uuid: String, destFile: File) {
        val uid = uid() ?: return
        try {
            destFile.parentFile?.mkdirs()
            imageRef(uid, uuid).getFile(destFile).await()
        } catch (e: StorageException) {
            if (e.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                Log.w(TAG, "downloadImage failed for $uuid", e)
            }
        } catch (e: Exception) {
            Log.w(TAG, "downloadImage failed for $uuid", e)
        }
    }
}
