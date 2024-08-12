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

class AddItemFragment : DialogFragment() {

    private lateinit var nameField: EditText
    private lateinit var rateField: EditText
    private lateinit var noteField: EditText
    private lateinit var buttonAdd: Button

    var onItemAdded: ((String, Int, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.additem, container, false)

        nameField = view.findViewById(R.id.nameField)
        rateField = view.findViewById(R.id.rateField)
        noteField = view.findViewById(R.id.noteField)

        buttonAdd = view.findViewById(R.id.doneButton)

        buttonAdd.setOnClickListener {
            val nameT = nameField.text.toString().trim()
            val rateT = rateField.text.toString().trim()
            val noteT = noteField.text.toString().trim()

            if (nameT.isNotEmpty() and rateT.isNotEmpty() and noteT.isNotEmpty()) {
                onItemAdded?.invoke(nameT, rateT.toInt(), noteT)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }

        return view
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