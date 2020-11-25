package com.example.chattingzone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val toolbar: Toolbar = findViewById(R.id.toolbar_Login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, welcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        mAuth = FirebaseAuth.getInstance()
        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val mail: String = email_login.text.toString()
        val password: String = password_login.text.toString()

        when {

            mail.equals("") -> {
                Toast.makeText(this, "please enter your mail!!", Toast.LENGTH_SHORT).show()
            }
            password.equals("") -> {
                Toast.makeText(this, "please enter your password!!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
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