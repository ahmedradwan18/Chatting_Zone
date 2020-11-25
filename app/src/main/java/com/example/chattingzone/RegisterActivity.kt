package com.example.chattingzone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_Register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, welcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        mAuth = FirebaseAuth.getInstance()
        register_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = username_register.text.toString()
        val mail: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        when {
            username.equals("") -> {
                Toast.makeText(this, "please enter your username!!", Toast.LENGTH_SHORT).show()
            }
            mail.equals("") -> {
                Toast.makeText(this, "please enter your mail!!", Toast.LENGTH_SHORT).show()
            }
            password.equals("") -> {
                Toast.makeText(this, "please enter your password!!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseUserID = mAuth.currentUser!!.uid
                            refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                                .child(firebaseUserID)

                            val userHashMap = HashMap<String, Any>()
                            userHashMap["uid"]=firebaseUserID
                            userHashMap["username"]=username
                            userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/chatting-zone-8bac5.appspot.com/o/profile_imge.png?alt=media&token=5ec99560-2656-420f-a178-0c68df13f88d"
                            userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/chatting-zone-8bac5.appspot.com/o/cover_image.jpg?alt=media&token=1c5928df-db2a-46ab-bed4-88ae7337bc18"
                            userHashMap["status"]="offline"
                            userHashMap["search"]=username.toLowerCase()
                            userHashMap["facebook"]="https://m.facebook.com"
                            userHashMap["instagram"]="https://m.instagram.com"

                            refUsers.updateChildren(userHashMap).addOnCompleteListener{task ->
                                if(task.isSuccessful){
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }

                            }


                        } else {
                            Toast.makeText(
                                this,
                                "Error Message : " + task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
            }
        }

    }
}