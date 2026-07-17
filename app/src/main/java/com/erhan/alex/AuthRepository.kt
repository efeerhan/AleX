package com.erhan.alex

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Wraps FirebaseAuth + Credential Manager (the modern replacement for GoogleSignInClient).
 * minSdk 34 makes Credential Manager fully viable, so there's no reason to use the legacy API.
 */
object AuthRepository {

    val currentUser: FirebaseUser?
        get() = Firebase.auth.currentUser

    /** Launches the Google account chooser and signs the user into Firebase. Throws on failure. */
    suspend fun signIn(activity: Activity): FirebaseUser {
        val googleOption = GetSignInWithGoogleOption
            .Builder(activity.getString(R.string.default_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        val result = CredentialManager.create(activity).getCredential(activity, request)
        val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)

        val authResult = Firebase.auth.signInWithCredential(firebaseCredential).await()
        return authResult.user ?: throw IllegalStateException("Sign-in succeeded but no Firebase user")
    }

    suspend fun signOut(context: Context) {
        Firebase.auth.signOut()
        try {
            CredentialManager.create(context)
                .clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
            // Clearing the credential state is best-effort; sign-out already succeeded.
        }
    }
}
