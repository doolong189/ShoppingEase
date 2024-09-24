package com.hoanglong180903.shoppingease

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.hoanglong180903.shoppingease.adapter.MessageAdapter
import com.hoanglong180903.shoppingease.databinding.ActivityMainBinding
import com.hoanglong180903.shoppingease.viewmodel.UserViewModel
import com.hoanglong180903.shoppingease.adapter.UserAdapter
import com.hoanglong180903.shoppingease.listener.OnClickItemUserChat
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.ui.activity.ChatActivity
import com.hoanglong180903.shoppingease.ui.fragment.MessageFragment
import com.hoanglong180903.shoppingease.ui.fragment.NotificationFragment
import com.hoanglong180903.shoppingease.ui.fragment.ShoppingFragment
import com.hoanglong180903.shoppingease.ui.fragment.UserFragment
import com.hoanglong180903.shoppingease.utils.Bundles
import com.hoanglong180903.shoppingease.utils.MySharedPreferences

class MainActivity : AppCompatActivity(){
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()

    }
    private fun setUpNavigation(){
        setSupportActionBar(binding.mainToolBar)
        binding.mainBottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    replaceFragment(ShoppingFragment())
                    true
                }
                R.id.navMessenger -> {
                    replaceFragment(MessageFragment())
                    true
                }
                R.id.navNotification -> {
                    replaceFragment(NotificationFragment())
                    true
                }
                R.id.navUser -> {
                    replaceFragment(UserFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(ShoppingFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.main_frame, fragment)
        transaction.commit()
    }
}