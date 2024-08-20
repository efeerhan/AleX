package com.erhan.alex

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.io.File

class ViewItemFragment : DialogFragment() {

    private lateinit var imageField: ImageView
    private lateinit var nameField: TextView
    private lateinit var whereField: TextView
    private lateinit var kindField: TextView
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

        imageField = view.findViewById(R.id.imageField)
        val file = File(context?.filesDir?.path, "images").resolve("IMG_"+arguments?.getInt("pic")+".jpg")
        imageField.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))

        val name = arguments?.getString("name")

        whereField = view.findViewById(R.id.whereView)
        val whereText = getString(R.string.viewWherePrefix)+" "+arguments?.getString("where")
        val where = arguments?.getString("where")
        whereField.text = whereText

        kindField = view.findViewById(R.id.kindView)
        val kindText = getString(R.string.viewKindPrefix)+" "+arguments?.getString("kind")
        val kind = arguments?.getString("kind")
        kindField.text = kindText

        dateField = view.findViewById(R.id.dateView)
        val dateText = getString(R.string.viewDatePrefix)+" "+arguments?.getString("date")
        dateField.text = dateText

        rateField = view.findViewById(R.id.rateView)
        val rating = arguments?.getInt("rating")
        val ratingText = getString(R.string.viewRatingPrefix)+" "+rating.toString()
        rateField.text = ratingText

        noteField = view.findViewById(R.id.noteView)
        val notes = arguments?.getString("notes")
        val notesText = "\""+notes+"\""
        noteField.text = notesText

        val pic = arguments?.getInt("pic")

        buttonEdit = view.findViewById(R.id.editButton)
        buttonDelete = view.findViewById(R.id.deleteButton)

        buttonEdit.setOnClickListener {
            val editItemFragment = AddItemFragment()
            val bundleEdit = Bundle()
            bundleEdit.putString("name", name)
            bundleEdit.putString("where", where)
            bundleEdit.putString("kind", kind)
            if (rating != null) {
                bundleEdit.putInt("rating", rating)
            }
            bundleEdit.putString("notes", notes)
            if (pic != null) {
                bundleEdit.putInt("pic", pic)
            }
            editItemFragment.arguments = bundleEdit
            editItemFragment.onItemAdded = { nameT, whereT, kindT, rateT, noteT ->
                if (name != null) {
                    editItem(nameT, whereT, kindT, rateT, noteT, "temporary")
                }
            }
            activity?.let { it1 -> editItemFragment.show(it1.supportFragmentManager, "AddItemFragment") }
        }
        buttonDelete.setOnClickListener {
            val ysf = YouSureFragment()
            val bundleDelete = Bundle()
            bundleDelete.putInt("id", (arguments?.getInt("id") as Int))
            ysf.arguments = bundleDelete
            activity?.let { it1 -> ysf.show(it1.supportFragmentManager, "YouSureFragment") }
        }

        return view
    }

    private fun editItem(newName: String, newWhere: String, newKind: String, newRating: Int, newNotes: String, newDate: String) {
        val entryViewModel = ViewModelProvider(this)[EntryViewModel::class.java]
        val id = arguments?.getInt("id")
        if (id != null) {
            entryViewModel.update(id, newName, newWhere, newKind, newRating, newNotes, "temporary")
        }
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