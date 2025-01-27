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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.io.File

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
            val id = arguments?.getInt("id")
            if (id != null) {
                deleteItem(id)
            }
            parentFragmentManager.apply {
                findFragmentByTag("YouSureFragment")?.let {
                    beginTransaction().remove(it).commit()
                }

                findFragmentByTag("ViewItemFragment")?.let {
                    beginTransaction().remove(it).commit()
                }
            }
        }

        buttonNo.setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun deleteItem(id: Int) {
        val db = AppDatabase.getDatabase(requireContext().applicationContext)
        val dao = db.entryDao()
        val file = File(context?.filesDir?.path, "images").resolve("IMG_"+dao.getPicByID(id).toString()+".jpg")
        file.delete()
        val entryViewModel = ViewModelProvider(this)[EntryViewModel::class.java]
        entryViewModel.delete(id)
        val howMany = "You've had "+dao.getCount().toString()+" unique brewskis."
        val howManyView: TextView? = activity?.findViewById<TextView>(R.id.howMany)
        howManyView?.text = howMany
        if ( howManyView == null ) {
            Log.i("entryViewCheck","couldn't find the proper view sorry")
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