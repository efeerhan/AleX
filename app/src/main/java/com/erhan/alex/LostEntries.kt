package com.erhan.alex

/**
 * Entries 158-310 were logged on the Z Flip and lost with it, unrecoverably. The 157 surviving
 * records (recovered from the S21) are chronologically entries 1-157, so the row count is no
 * longer the lifetime count: anything logged after the last surviving entry continues from 311
 * rather than 158.
 */
const val LOST_ENTRIES = 153

/** Date of the 157th (last surviving) entry. Anything dated after this is post-loss. */
const val LOST_CUTOFF_DATE = "11/14/2025"

/** "MM/dd/yyyy" -> "yyyy/MM/dd", so dates sort and compare lexicographically. */
fun representDateSortably(date: String): String {
    val month = date.substring(0, 2)
    val day = date.substring(3, 5)
    val year = date.substring(6)
    return "$year/$month/$day"
}

/** True if [date] falls after the last surviving pre-loss entry. */
fun isAfterLoss(date: String): Boolean =
    representDateSortably(date) > representDateSortably(LOST_CUTOFF_DATE)
