package com.erhan.alex

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class ViewItemFragment : DialogFragment() {

    private lateinit var nameField: TextView
    private lateinit var dateField: TextView
    private lateinit var rateField: TextView
    private lateinit var noteField: TextView
    private lateinit var buttonEdit: Button
    private lateinit var buttonDelete: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.viewitem, container, false)

        nameField = view.findViewById(R.id.nameView)
        nameField.text = arguments?.getString("name")

        dateField = view.findViewById(R.id.dateView)
        dateField.text = arguments?.getString("date")

        rateField = view.findViewById(R.id.rateView)
        rateField.text = arguments?.getString("rating")

        noteField = view.findViewById(R.id.noteView)
        noteField.text = arguments?.getString("notes")

        buttonEdit = view.findViewById(R.id.editButton)
        buttonDelete = view.findViewById(R.id.deleteButton)

        buttonEdit.setOnClickListener {

        }
        buttonDelete.setOnClickListener {

        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}