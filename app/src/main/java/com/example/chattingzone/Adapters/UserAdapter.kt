package com.example.chattingzone.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingzone.MainActivity
import com.example.chattingzone.MessageChatActivity
import com.example.chattingzone.Models.Users
import com.example.chattingzone.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    context: Context,
    users: List<Users>,
    isChatChecked: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    private val context: Context
    private val users: List<Users>
    private var isChatChecked: Boolean

    init {
        this.context = context
        this.users = users
        this.isChatChecked = isChatChecked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.user_search_item, parent, false);
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = users[position]
        holder.username.text = user.username

        holder.itemView.setOnClickListener {

            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("What Do You Want ?")
            builder.setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->
                if (i == 0) {
                    val intent = Intent(context, MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.uid)
                    context.startActivity (intent)

                }
                if (i == 1) {
                }
            })
            builder.show()

        }










        if (user.profile.isEmpty()) {
            holder.profileImage.setImageResource(R.drawable.profile_imge);
        } else {
            Picasso.get().load(user.profile).placeholder(R.drawable.profile_imge)
                .into(holder.profileImage)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView
        var profileImage: CircleImageView
        var onlineImage: CircleImageView
        var offlineImage: CircleImageView
        var lastMessage: TextView

        init {
            username = itemView.findViewById(R.id.username_item)
            profileImage = itemView.findViewById(R.id.profile_image_item)
            onlineImage = itemView.findViewById(R.id.image_online_item)
            offlineImage = itemView.findViewById(R.id.image_offline_item)
            lastMessage = itemView.findViewById(R.id.last_message_item)
        }
    }


}