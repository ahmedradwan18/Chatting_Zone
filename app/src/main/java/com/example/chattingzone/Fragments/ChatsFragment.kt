package com.example.chattingzone.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingzone.Adapters.UserAdapter
import com.example.chattingzone.Models.ChatList
import com.example.chattingzone.Models.Users
import com.example.chattingzone.Notifications.Token
import com.example.chattingzone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class ChatsFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var users: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    private var recyclerView: RecyclerView? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recycler_chatList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref =
            FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (ds in snapshot.children) {
                    val chatList = ds.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }
                retreiveChatList()
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    private fun updateToken(token: String?) {

        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1=Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retreiveChatList() {
        users = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (users as ArrayList).clear()

                for (ds in snapshot.children) {
                    val user = ds.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!) {
                        if (user!!.uid == eachChatList.ID) {
                            (users as ArrayList<Users>).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (users as ArrayList<Users>), true)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}