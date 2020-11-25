package com.example.chattingzone.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingzone.Adapters.UserAdapter
import com.example.chattingzone.Models.Users
import com.example.chattingzone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var users: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEdt: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.search_recycler)
        searchEdt = view.findViewById(R.id.searchUsersEdt)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        users = ArrayList()

        retreiveAllUsers()

        searchEdt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUser(p0.toString().toLowerCase(Locale.ROOT))
            }

        })


        return view
    }

    private fun retreiveAllUsers() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (users as ArrayList<Users>).clear()

                if (searchUsersEdt!!.text.toString() == "") {
                    for (snap in snapshot.children) {

                        val user: Users? = snap.getValue(Users::class.java)
                        //Toast.makeText(context, user!!.username+"\n"+userID, Toast.LENGTH_SHORT).show()

                        if (user!!.uid != userID) {
                            //Toast.makeText(context, "hi "+user!!.profile, Toast.LENGTH_SHORT).show()

                            (users as ArrayList<Users>).add(user)

                        }

                    }
                }

                userAdapter = UserAdapter(context!!, users!!, false)
                recyclerView!!.adapter = userAdapter
            }


        })
    }

    private fun searchForUser(str: String) {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search").startAt(str).endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (users as ArrayList<Users>).clear()
                for (snap in snapshot.children) {

                    val user: Users? = snap.getValue(Users::class.java)
                    if (user!!.uid != userID) {
                        (users as ArrayList<Users>).add(user)

                    }
                }
                userAdapter = UserAdapter(context!!, users!!, false)
                recyclerView!!.adapter = userAdapter

            }

        })
    }

}