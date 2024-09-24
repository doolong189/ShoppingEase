package com.hoanglong180903.shoppingease.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.app.MyApplication
import com.hoanglong180903.shoppingease.databinding.ItemReceiverBinding
import com.hoanglong180903.shoppingease.databinding.ItemSenderBinding
import com.hoanglong180903.shoppingease.model.MessageModel
import com.hoanglong180903.shoppingease.utils.MySharedPreferences


class ChatAdapter(private val context : Context, private val messageList: List<MessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2
    private var mySharedPreferences: MySharedPreferences = MySharedPreferences(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sender, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
//        return if (FirebaseAuth.getInstance().uid == message.senderId) {
//            ITEM_SENT
//        } else {
//            ITEM_RECEIVE
//        }
        return if (mySharedPreferences.userId == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentViewHolder) {
            holder.binding.itemSentTextview.text = message.messageText
            if (message.messageText == "Photo" || message.messageText
                == "camera") {
                holder.binding.itemSendPhotoImage.visibility = View.VISIBLE
                holder.binding.itemSentTextview.visibility = View.GONE
                holder.binding.linearLayout.setBackgroundResource(R.drawable.border_shadow)
                Glide.with(holder.itemView.context).load(message.messageImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemSendPhotoImage)
            }else if (message.messageText == "Mặt hàng này còn chứ?"){
                holder.binding.itemSendPhotoImage.visibility = View.VISIBLE
                holder.binding.itemSentTextview.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.messageImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemSendPhotoImage)
            }
        } else if (holder is ReceiverViewHolder) {
            holder.binding.itemReceiveTextview.text = message.messageText
            if (message.messageText == "Photo" || message.messageText
                == "camera"
            ) {
                holder.binding.itemReceivePhotoImage.visibility = View.VISIBLE
                holder.binding.itemReceiveTextview.visibility = View.GONE
                holder.binding.linearLayout2.setBackgroundResource(R.drawable.border_shadow)
                Glide.with(holder.itemView.context).load(message.messageImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemReceivePhotoImage)
            }
            else if (message.messageText == "Mặt hàng này còn chứ?"){
                holder.binding.itemReceivePhotoImage.visibility = View.VISIBLE
                holder.binding.itemReceiveTextview.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(message.messageImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.itemReceivePhotoImage)
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemSenderBinding = ItemSenderBinding.bind(itemView)
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemReceiverBinding = ItemReceiverBinding.bind(itemView)
    }
}