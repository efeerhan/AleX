package com.erhan.alex

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

private const val TAG = "AccountFragment"

class AccountFragment : DialogFragment() {

    private lateinit var statusView: TextView
    private lateinit var signInButton: Button
    private lateinit var signOutButton: Button
    private lateinit var restoreButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.accountdialog, container, false)

        statusView = view.findViewById(R.id.accountStatus)
        signInButton = view.findViewById(R.id.signInButton)
        signOutButton = view.findViewById(R.id.signOutButton)
        restoreButton = view.findViewById(R.id.restoreButton)

        refreshUi()

        signInButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    AuthRepository.signIn(requireActivity())
                    // Same shared instance MainActivity observes.
                    viewModel().onSignedIn()
                    refreshUi()
                } catch (e: GetCredentialCancellationException) {
                    // User backed out of the account chooser; not an error worth reporting.
                    Log.i(TAG, "Sign-in cancelled by user")
                } catch (e: Exception) {
                    // Sign-in failures are almost always config (missing SHA-1 fingerprint,
                    // stale google-services.json), so log the real cause rather than swallow it.
                    Log.w(TAG, "Sign-in failed", e)
                    Toast.makeText(context, R.string.signInFailed, Toast.LENGTH_SHORT).show()
                }
            }
        }

        signOutButton.setOnClickListener {
            lifecycleScope.launch {
                AuthRepository.signOut(requireContext())
                refreshUi()
            }
        }

        restoreButton.setOnClickListener {
            Toast.makeText(context, R.string.restoreStarted, Toast.LENGTH_SHORT).show()
            viewModel().restoreFromCloud()
        }

        return view
    }

    private fun viewModel(): EntryViewModel =
        ViewModelProvider(requireActivity())[EntryViewModel::class.java]

    private fun refreshUi() {
        val user = AuthRepository.currentUser
        if (user == null) {
            statusView.setText(R.string.accountSignedOut)
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
            restoreButton.visibility = View.GONE
        } else {
            statusView.text = getString(R.string.accountSignedInPrefix) + (user.email ?: user.uid)
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
            restoreButton.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
