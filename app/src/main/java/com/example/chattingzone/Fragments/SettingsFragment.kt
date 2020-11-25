package com.example.chattingzone.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chattingzone.Models.Users
import com.example.chattingzone.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {
    var userRefrence: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageReference: StorageReference? = null
    private var coverChecker: String? = null
    private var socialChecker: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userRefrence =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageReference = FirebaseStorage.getInstance().reference.child("User Images")
        userRefrence!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)

                    if (context != null) {
                        view.username_settings.text = user!!.username
                        Picasso.get().load(user.profile).into(view.profile_image_settings)
                        Picasso.get().load(user.cover).into(view.cover_image_settings)
                    }

                }
            }


        })
        view.profile_image_settings.setOnClickListener {
            pickImage()
        }

        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }

        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }

        view.cover_image_settings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }


        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if (socialChecker == "website") {
            builder.setTitle("Write Url : ")
        } else {
            builder.setTitle("Write Username : ")
        }
        val editText = EditText(context)
        if (socialChecker == "website") {
            editText.hint = "e.g www.Google.com"
        } else {
            editText.hint = "e.g ahmed radwan"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialogInterface, i ->
            val str = editText.text.toString()
            if (str == null) {
                Toast.makeText(context, "please write something...", Toast.LENGTH_SHORT)

            } else {
                saveSocialLink(str)
            }


        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()
        when (socialChecker) {
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"

            } "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$str"

            } "website" -> {
                mapSocial["website"] = "https://$str"

            }

        }
        userRefrence!!.updateChildren(mapSocial).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                Toast.makeText(context, "updated successfully...", Toast.LENGTH_SHORT)

            }
        }

    }


    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    @SuppressLint("ShowToast")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Uploaded...", Toast.LENGTH_SHORT)
            uploadImage()

        }
    }

    private fun uploadImage() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading..please wait!")
        progressBar.setCancelable(false)
        progressBar.show()
        if (imageUri != null) {
            val fileRef = storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    if (coverChecker == "cover") {
                        val mapCover = HashMap<String, Any>()
                        mapCover["cover"] = url
                        userRefrence!!.updateChildren(mapCover)
                        coverChecker = ""
                    } else {
                        val mapProfile = HashMap<String, Any>()
                        mapProfile["profile"] = url
                        userRefrence!!.updateChildren(mapProfile)
                        coverChecker = ""

                    }
                    progressBar.dismiss()
                }
            }

        }

    }

}