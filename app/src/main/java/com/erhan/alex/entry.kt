package com.erhan.alex

import android.media.Image
import java.util.Date

data class entry (
    var name: String,
    var date: Date,
    var rating: Int,
    var notes: String,
    var pic: Int
)