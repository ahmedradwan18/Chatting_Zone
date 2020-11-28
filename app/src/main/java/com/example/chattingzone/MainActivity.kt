package com.example.chattingzone

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.chattingzone.Fragments.ChatsFragment
import com.example.chattingzone.Fragments.SearchFragment
import com.example.chattingzone.Fragments.SettingsFragment
import com.example.chattingzone.Models.Chat
import com.example.chattingzone.Models.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var refUsers: DatabaseReference? = null
    var ref: DatabaseReference? = null
    var currentUser: FirebaseUser? = null

    var progressBar: ProgressDialog? = null
    //private var progress: ProgressBar? = null

    var countUnreadMessages = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))






        progressBar = ProgressDialog(this)
        progressBar!!.setCancelable(false)
        progressBar!!.setMessage("Loading Your Account..")

        progressBar!!.show()
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = viewPagerAdapter(supportFragmentManager)

        currentUser = FirebaseAuth.getInstance().currentUser

        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
            .child(currentUser!!.uid)

        ref = FirebaseDatabase.getInstance().reference.child("Chats")

        ref!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val viewPagerAdapter = viewPagerAdapter(supportFragmentManager)


                    for (ds in snapshot.children) {
                        val chat = ds.getValue(Chat::class.java)

                        if (chat!!.receiver == currentUser!!.uid && !chat.isSeen) {
                            countUnreadMessages += 1
                        }
                    }



                    if (countUnreadMessages == 0) {

                        progressBar!!.dismiss()


                        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                        viewPagerAdapter.addFragment(SearchFragment(), "Search")
                        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

                        viewPager.adapter = viewPagerAdapter
                        tabLayout.setupWithViewPager(viewPager)
                        Toast.makeText(this@MainActivity, "1", Toast.LENGTH_SHORT).show()
                    } else {
                        progressBar!!.dismiss()

                        viewPagerAdapter.addFragment(
                            ChatsFragment(),
                            "($countUnreadMessages) Chats"
                        )
                        viewPagerAdapter.addFragment(SearchFragment(), "Search")
                        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

                        viewPager.adapter = viewPagerAdapter
                        tabLayout.setupWithViewPager(viewPager)

                    }

                } else {
                    progressBar!!.dismiss()

                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                    viewPagerAdapter.addFragment(SearchFragment(), "Search")
                    viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

                    viewPager.adapter = viewPagerAdapter
                    tabLayout.setupWithViewPager(viewPager)

                }


            }

            override fun onCancelled(error: DatabaseError) {


            }
        })


        // display username and image
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    username.text = user!!.username
                    Picasso.get().load(user.profile).placeholder(R.drawable.profile_imge)
                        .into(profile_image)
                }

            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, welcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }


    internal class viewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {
        private val fragments: ArrayList<Fragment> = ArrayList()
        private val titles: ArrayList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {

            return fragments[position]
        }


        override fun getCount(): Int {

            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }
    }

    private fun updateStatus(status: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!.uid)
        val hashmap = HashMap<String, Any?>()
        hashmap["status"] = status
        ref.updateChildren(hashmap)

    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")

    }
}