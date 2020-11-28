package com.example.chattingzone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingzone.Models.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_prof.*

class VisitUserProfActivity : AppCompatActivity() {
    private var userVisitID: String = ""
    var user:Users?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_prof)
        userVisitID = intent.getStringExtra("visit_id")
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                         user = snapshot.getValue(Users::class.java)
                        username_display.text = user!!.username
                        Picasso.get().load(user!!.profile).into(profile_image_display)
                        Picasso.get().load(user!!.cover).into(cover_image_display)
                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
        set_facebook_display.setOnClickListener {
            val uri = Uri.parse(user!!.facebook)
            val intent= Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
            finish()
        }

        set_instagram_display.setOnClickListener {
            val uri = Uri.parse(user!!.instagram)
            val intent= Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
            finish()
        }
        sendMessageBtn.setOnClickListener {
            val intent = Intent(this, MessageChatActivity::class.java)
            intent.putExtra("visit_id",user!!.uid)
            startActivity(intent)
            finish()
        }

    }
}