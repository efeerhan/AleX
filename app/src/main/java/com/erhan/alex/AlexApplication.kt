package com.erhan.alex

import android.app.Application
import com.google.firebase.FirebaseApp

class AlexApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // The google-services plugin auto-initializes Firebase via a manifest-merged
        // ContentProvider; this explicit call documents intent and is a harmless no-op.
        FirebaseApp.initializeApp(this)
    }
}
