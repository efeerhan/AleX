package com.erhan.alex

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class AddItemFragment : DialogFragment() {

    private lateinit var nameField: EditText
    private lateinit var rateField: EditText
    private lateinit var noteField: EditText
    private lateinit var buttonAdd: Button
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private val cameraRequestCode = 101

    var onItemAdded: ((String, Int, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.additem, container, false)

        val imageField: ImageView = view.findViewById(R.id.imageField)
        nameField = view.findViewById(R.id.nameField)
        rateField = view.findViewById(R.id.rateField)
        noteField = view.findViewById(R.id.noteField)

        val picInt = arguments?.getInt("pic")
        if ( picInt != null ) {
            val file = File(context?.filesDir?.path, "images").resolve("IMG_$picInt.jpg")
            imageField.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            val name = arguments?.getString("name")
            val rating = arguments?.getInt("rating")
            val notes = arguments?.getString("notes")
            nameField.setText(name)
            rateField.setText(rating.toString())
            noteField.setText(notes)
        }

        buttonAdd = view.findViewById(R.id.doneButton)

        buttonAdd.setOnClickListener {
            val nameT = nameField.text.toString().trim()
            val rateT = rateField.text.toString().trim()
            val noteT = noteField.text.toString().trim()

            if (nameT.isNotEmpty() and rateT.isNotEmpty() and noteT.isNotEmpty()) {
                onItemAdded?.invoke(nameT, rateT.toInt(), noteT)
                if ( picInt != null ) {
                    parentFragmentManager.apply {
                        findFragmentByTag("AddItemFragment")?.let {
                            beginTransaction().remove(it).commit()
                        }

                        findFragmentByTag("ViewItemFragment")?.let {
                            beginTransaction().remove(it).commit()
                        }
                    }
                }
                dismiss()
            } else {
                Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                saveImageToInternalStorage(imageBitmap)
                imageField.setImageBitmap(imageBitmap)
            }
        }

        imageField.setOnClickListener {
            if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1, android.Manifest.permission.CAMERA) }
                != PackageManager.PERMISSION_GRANTED) {
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(android.Manifest.permission.CAMERA), cameraRequestCode) }
            }
            else {
                openCamera()
            }
        }
        return view
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val db = AppDatabase.getDatabase(requireContext().applicationContext)
        val dao = db.entryDao()
        var maxPic = (dao.getMaxPic())
        if ( dao.getCount() > 0 ) {
            maxPic++
        }
        // Two cases: 1. new item being added 2. old item being modified. Need old pic value for edit.
        val picInt = arguments?.getInt("pic")
        val filename: String
        if ( picInt == null ) {
            // new
            Log.i("entrySavePic","this is new and pic is $maxPic")
            filename = "IMG_${maxPic}.jpg"
        } else {
            //edit
            Log.i("entrySavePic","this is edit and pic is $picInt")
            filename = "IMG_$picInt.jpg"
        }
        var fileOutputStream: FileOutputStream? = null
        try {
            Files.createDirectories(Paths.get(context?.filesDir?.path+"/images/"));
            val file = File(context?.filesDir?.path+"/images/", filename)
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileOutputStream?.close()
        }
        return filename
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