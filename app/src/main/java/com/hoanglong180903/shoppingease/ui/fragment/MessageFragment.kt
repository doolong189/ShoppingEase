package com.hoanglong180903.shoppingease.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.adapter.MessageAdapter
import com.hoanglong180903.shoppingease.adapter.UserAdapter
import com.hoanglong180903.shoppingease.databinding.FragmentMessageBinding
import com.hoanglong180903.shoppingease.listener.OnClickItemUserChat
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.ui.activity.ChatActivity
import com.hoanglong180903.shoppingease.utils.Bundles
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.viewmodel.UserViewModel

class MessageFragment : Fragment() , OnClickItemUserChat {
    private lateinit var binding : FragmentMessageBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var mySharedPreferences: MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentMessageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getUser()
    }

    private fun init(){
        viewModel = ViewModelProvider(this, UserViewModel.UserViewModelFactory(requireActivity().application))[UserViewModel::class.java]
        mySharedPreferences = MySharedPreferences(requireContext())
    }

    private fun getUser(){
        viewModel.mUser.observe(viewLifecycleOwner) {
            binding.rcUser.adapter = UserAdapter(it,this)
        }
        viewModel.mUser.observe(viewLifecycleOwner, Observer { users ->
            if (users.isEmpty()) {
                binding.messageTvMessageEmpty.visibility = View.VISIBLE
                binding.rcChatUser.visibility = View.GONE
            }else{
                binding.rcChatUser.adapter = MessageAdapter(users,requireContext())
                binding.rcChatUser.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.messageTvMessageEmpty.visibility = View.GONE
            }
        })
        viewModel.getUsers(mySharedPreferences.userId.toString())
    }

    override fun onClickItem(user: User) {
        val bundle = Bundle().apply {
            Bundles.bundleData(user,this)
        }
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}