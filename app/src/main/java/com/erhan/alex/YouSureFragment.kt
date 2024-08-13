package com.erhan.alex

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

class YouSureFragment : DialogFragment() {

    private lateinit var buttonYes: Button
    private lateinit var buttonNo: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.deleteitem, container, false)

        buttonYes = view.findViewById(R.id.yesButton)
        buttonNo = view.findViewById(R.id.noButton)

        buttonYes.setOnClickListener {
        }

        buttonNo.setOnClickListener {

        }
        return view
    }

    private fun deleteItem(entry: Entry) {
        val entryViewModel = ViewModelProvider(this)[EntryViewModel::class.java]
        val updatedEntry = entry.copy(name = "Updated Item")
        entryViewModel.update(updatedEntry)
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