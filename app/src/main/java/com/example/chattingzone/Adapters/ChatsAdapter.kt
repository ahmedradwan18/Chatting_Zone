package com.example.chattingzone.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingzone.Adapters.ChatsAdapter.viewHolder
import com.example.chattingzone.Models.Chat
import com.example.chattingzone.R
import com.example.chattingzone.ViewFullImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    context: Context,
    chatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<viewHolder?>() {

    private val context: Context
    private val chatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.context = context
        this.chatList = chatList
        this.imageUrl = imageUrl
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView? = null
        var showTextMessage: TextView? = null
        var leftImageView: ImageView? = null
        var rightImageView: ImageView? = null
        var textSeen: TextView? = null

        init {
            profileImage = itemView.findViewById(R.id.profile_image)
            showTextMessage = itemView.findViewById(R.id.show_text_message)
            leftImageView = itemView.findViewById(R.id.image_left)
            rightImageView = itemView.findViewById(R.id.image_right)
            textSeen = itemView.findViewById(R.id.text_seen)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): viewHolder {

        return if (position == 1) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false);
            return viewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false);
            return viewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val chat: Chat = chatList[position]

        Picasso.get().load(imageUrl).into(holder.profileImage)


        // Image Messages
        if (chat.message == "sent you an image." && chat.url != "") {

            //image message - right side
            if (chat.sender == firebaseUser.uid) {
                holder.showTextMessage!!.visibility = View.GONE
                holder.rightImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.rightImageView)
                holder.rightImageView!!.setOnClickListener {
                    val options = arrayOf(
                        "View Full Image"
                        , "Delete Image", "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(
                        options
                    ) { _, i ->
                        if (i == 0) {
                            val intent = Intent(context, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            context.startActivity(intent)
                        } else if (i == 1) {
                            deleteMessage(position, holder)
                        }
                    }
                    builder.show()
                }
            }


            //image message - left side

            else if (chat.sender != firebaseUser.uid) {
                holder.showTextMessage!!.visibility = View.GONE
                holder.leftImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.leftImageView)

                holder.leftImageView!!.setOnClickListener {
                    val options = arrayOf(
                        "View Full Image"
                        , "Cancel"
                    )
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(
                        options,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (i == 0) {
                                val intent = Intent(context, ViewFullImageActivity::class.java)
                                intent.putExtra("url", chat.url)
                                context.startActivity(intent)
                            }
                        })
                    builder.show()
                }
            }

        }

        // Text messages
        else {
            holder.showTextMessage!!.visibility = View.VISIBLE
            holder.showTextMessage!!.text = chat.message
            if (firebaseUser!!.uid == chat.sender) {

                holder.showTextMessage!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message", "Cancel"
                    )
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(
                        options
                    ) { dialogInterface, i ->
                        if (i == 0) {
                            deleteMessage(position, holder)
                        }
                    }
                    builder.show()
                }

            }
        }

// sent and seen message
        if (position == chatList.size - 1) {

            if (chat.isSeen) {
                holder.textSeen!!.text = "Seen"
                if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {

                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            } else {
                holder.textSeen!!.text = "Sent"
                if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {

                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }

        } else {
            holder.textSeen!!.visibility = View.GONE
        }


    }

    override fun getItemViewType(position: Int): Int {
        // it means that its the sender who send message
        return if (chatList[position].sender == firebaseUser!!.uid) {
            1
        }
        // it means that its the Receiver who send message
        else {
            0
        }
    }

    private fun deleteMessage(position: Int, holder: ChatsAdapter.viewHolder) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(chatList[position].messageId)



        ref.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Deleted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Something went wrong , please try later..",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }
}