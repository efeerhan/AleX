package com.erhan.alex

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Durable list of entries whose cloud push hasn't been confirmed yet.
 *
 * Pushes are fire-and-forget, and the signed-out case used to return from [CloudSyncRepository]
 * without a log line, an exception, or any record that a write was owed — which is how a week of
 * entries stayed phone-only with nothing noticing. An entry goes on this list the moment it's
 * written locally and comes off only when the cloud confirms, so a push lost to a signed-out
 * session, a dead network, or a killed process is still on the books for the next reconcile.
 *
 * Backed by SharedPreferences rather than a Room column deliberately: it survives process death
 * the same way, and it needs no schema migration on a database whose last migration is already
 * implicated in the duplicated-image mess.
 */
object PendingPushes {

    private const val PREFS = "cloud_sync_prefs"
    private const val KEY = "pending_pushes"

    private val liveCount = MutableLiveData<Int>()

    /** Number of entries still waiting to reach the cloud; drives the main-screen indicator. */
    val count: LiveData<Int> = liveCount

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    @Synchronized
    fun all(context: Context): Set<String> =
        // getStringSet's returned set must never be mutated or held onto — copy it.
        prefs(context).getStringSet(KEY, emptySet())?.toSet() ?: emptySet()

    @Synchronized
    fun mark(context: Context, uuid: String) {
        val next = all(context).toMutableSet()
        if (next.add(uuid)) write(context, next)
    }

    @Synchronized
    fun clear(context: Context, uuid: String) {
        val next = all(context).toMutableSet()
        if (next.remove(uuid)) write(context, next)
    }

    /** Seeds the indicator at app start, before any push has been attempted this session. */
    fun refresh(context: Context) = liveCount.postValue(all(context).size)

    private fun write(context: Context, next: Set<String>) {
        // apply(), not commit(): inserts run on the main thread and this must not touch disk there.
        prefs(context).edit().putStringSet(KEY, next).apply()
        liveCount.postValue(next.size)
    }
}
