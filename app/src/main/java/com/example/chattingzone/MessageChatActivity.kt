package com.example.chattingzone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingzone.Adapters.ChatsAdapter
import com.example.chattingzone.Models.Chat
import com.example.chattingzone.Models.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var adapter: ChatsAdapter? = null
    var chatList: List<Chat>? = null
    var reference: DatabaseReference? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar_Message_Chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, welcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }




        recyclerView = findViewById(R.id.recycler_Message_Chats)
        recyclerView.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager



        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        reference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                username_Message_Chat.text = user!!.username
                Picasso.get().load(user.profile).into(profile_image_Message_Chat)
                RetreiveMessages(firebaseUser!!.uid, userIdVisit, user.profile)
            }


        })

        attach_image_Message_Chat.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)

        }
        sendMessage_Message_Chat.setOnClickListener {
            val message = text_message_Message_Chat.text.toString()
            if (message == "") {
                Toast.makeText(
                    this,
                    "Please enter your message first !",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)

            }
            text_message_Message_Chat.setText("")

        }
        seenMessage(userIdVisit)
    }


    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key
        val messageMap = HashMap<String, Any?>()
        messageMap["sender"] = senderId
        messageMap["receiver"] = receiverId
        messageMap["message"] = message
        messageMap["isSeen"] = false
        messageMap["url"] = ""
        messageMap["messageId"] = messageKey
        reference.child("Chats").child(messageKey!!).setValue(messageMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference =
                        FirebaseDatabase.getInstance().reference.child("ChatList")
                            .child(firebaseUser!!.uid).child(userIdVisit)
                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {


                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatListReference.child("ID").setValue(userIdVisit)

                            }
                            val chatListReceiverReference =
                                FirebaseDatabase.getInstance().reference.child("ChatList")
                                    .child(userIdVisit).child(firebaseUser!!.uid)
                            chatListReceiverReference.child("ID").setValue(firebaseUser!!.uid)


                        }

                    })

                    // implement the push notification FCM later


                    val reference = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(firebaseUser!!.uid)

                }
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data!!.data != null) {

            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("please wait , image is loaded...")
            loadingBar.show()

            val fileUri = data!!.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl

            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageMap = HashMap<String, Any?>()
                    messageMap["sender"] = firebaseUser!!.uid
                    messageMap["receiver"] = userIdVisit
                    messageMap["message"] = "sent you an image."
                    messageMap["isSeen"] = false
                    messageMap["url"] = url
                    messageMap["messageId"] = messageId


                    ref.child("Chats").child(messageId!!).setValue(messageMap)
                    loadingBar.dismiss()
                }
            }


        }

    }

    private fun RetreiveMessages(senderID: String, receiverID: String?, imageUrl: String) {
        chatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()

                for (ss in snapshot.children) {
                    val chat = ss.getValue(Chat::class.java)

                    // to guarantee that this data is related to the sender and receiver only
                    if (chat!!.receiver.equals(senderID) && chat!!.sender.equals(receiverID)
                        || chat!!.sender.equals(senderID) && chat!!.receiver.equals(receiverID)
                    ) {

                        (chatList as ArrayList<Chat>).add(chat)

                    }
                    adapter = ChatsAdapter(
                        this@MessageChatActivity,
                        (chatList as ArrayList<Chat>),
                        imageUrl!!
                    )
                    recyclerView.adapter = adapter
                }

            }

        })
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String) {

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (ds in snapshot.children) {
                    val chat = ds.getValue(Chat::class.java)

                    if (chat!!.receiver == firebaseUser!!.uid &&
                        chat!!.sender == userId
                    ) {

                        val hashMap = HashMap<String, Any>()
                        hashMap["isSeen"] = true
                        ds.ref.updateChildren(hashMap)
                    }


                }


            }

            override fun onCancelled(error: DatabaseError) {


            }


        })


    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }

}