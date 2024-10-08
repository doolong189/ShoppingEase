package com.hoanglong180903.shoppingease.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.listener.OnClickItemUserChat
import com.hoanglong180903.shoppingease.model.User


class UserAdapter (private val users: List<User>,
                   private val listener: OnClickItemUserChat
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfile : ImageView = itemView.findViewById(R.id.item_asu_imageProfile)
        val tvName : TextView = itemView.findViewById(R.id.item_asu_tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_status_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        if (user.image == "No image") {
            holder.imageProfile.setImageResource(R.drawable.ic_launcher_foreground)
        }else{
            Glide.with(holder.itemView.context)
                .load(user.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageProfile)
        }
        holder.tvName.text = user.name
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)
        holder.imageProfile.setOnClickListener {
            listener.onClickItem(user)
        }
    }

    override fun getItemCount() = users.size

}