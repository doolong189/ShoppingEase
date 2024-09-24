package com.hoanglong180903.shoppingease.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hoanglong180903.shoppingease.MainActivity
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.adapter.ChatAdapter
import com.hoanglong180903.shoppingease.databinding.ActivityChatBinding
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.viewmodel.firebase.ChatViewModel
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private var senderRoom: String = ""
    private var receiverRoom: String = ""
    private lateinit var mySharedPreferences: MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        getDataFromBundle()
        actionOnClick()
    }

    private fun init(){
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        mySharedPreferences = MySharedPreferences(this)
    }

    private fun getDataFromBundle(){
        val bundle = intent.extras
        if (bundle != null) {
            val name = bundle.getString("name")
            val email = bundle.getString("email")
            val userId = bundle.getString("userId")
            val profileImage = bundle.getString("profileImage")
            senderRoom = mySharedPreferences.userId + userId
            receiverRoom = userId + mySharedPreferences.userId
            binding.tvTopChat.text = name
            if (profileImage == "No image") {
                binding.chatProfile.setImageResource(R.drawable.ic_launcher_foreground)
            } else {
                Glide.with(this)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.chatProfile)
            }
            requestSendMessage(userId.toString(), "", mySharedPreferences.userName.toString())
            getData(senderRoom)
        }
    }

    private fun actionOnClick(){
        binding.iconBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getData(senderRoom: String) {
        viewModel.users.observe(this, Observer { users ->
            binding.rcChat.adapter = ChatAdapter(this,users)
            binding.rcChat.layoutManager = LinearLayoutManager(
                applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
        })
        viewModel.fetchMessage(senderRoom)
    }

    private fun requestSendMessage(receiver: String, token: String, name: String) {
        val date = Date()
        val senderRoom = mySharedPreferences.userId + receiver
        val receiverRoom = receiver + mySharedPreferences.userId
        binding.imageSend.setOnClickListener {
            viewModel.sendMessage(
                binding.edChating.text.toString(),
                mySharedPreferences.userId!!,
                date.time,
                senderRoom,
                receiverRoom,
                token,
                applicationContext,
                name
            )
            binding.edChating.setText("")
        }
    }
}