package com.hoanglong180903.shoppingease.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import java.text.SimpleDateFormat
import java.util.Date

class MessageAdapter (private val users: List<User>,
    context : Context
                      ) : RecyclerView.Adapter<MessageAdapter.UserViewHolder>() {
    private var senderId : String = "";
    private var senderRoom : String = "";
    private var senderName : String = ""
    private var database: FirebaseDatabase? = null
    private var mySharedPreferences: MySharedPreferences = MySharedPreferences(context)

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfile : ImageView = itemView.findViewById(R.id.item_history_message_image)
        val tvName : TextView = itemView.findViewById(R.id.item_history_message_tvName)
        val tvTime : TextView = itemView.findViewById(R.id.item_history_message_tvTime)
        val tvMessage : TextView = itemView.findViewById(R.id.item_history_message_tvMessage)
        val linear : LinearLayout = itemView.findViewById(R.id.item_history_message_linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_message, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = users[position]
        senderId = mySharedPreferences.userId!!
        senderRoom = senderId + item._id
        database = FirebaseDatabase.getInstance()
        FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(senderRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val lastMsg = snapshot.child("lastMsg").getValue(String::class.java)
                        val time = snapshot.child("lastMsgTime").getValue(Long::class.java)!!
                        val dateFormat = SimpleDateFormat("hh:mm a")
                        holder.tvTime.text = dateFormat.format(Date(time))
                        holder.tvMessage.text = lastMsg
                        holder.linear.visibility = View.VISIBLE
                    } else {
//                        holder.tvMessage.visibility = View.GONE
//                        holder.imageProfile.visibility = View.GONE
//                        holder.tvTime.visibility = View.GONE
//                        holder.tvName.visibility = View.GONE
//                        holder.tvMessage.text = "Tap to chat"
                        holder.linear.visibility = View.GONE

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        holder.tvName.text = item.name
        Glide.with(holder.itemView.context).load(item.image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imageProfile)
//        holder.itemView.setOnClickListener {
//            listener.onClickItemToChat(item)
//        }
    }

    override fun getItemCount() = users.size

}