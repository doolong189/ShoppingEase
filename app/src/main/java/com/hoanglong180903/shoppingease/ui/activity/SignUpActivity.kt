package com.hoanglong180903.shoppingease.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hoanglong180903.shoppingease.databinding.ActivitySignUpBinding
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.repository.SignUpRepository
import com.hoanglong180903.shoppingease.utils.Resource
import com.hoanglong180903.shoppingease.utils.Status
import com.hoanglong180903.shoppingease.viewmodel.SignUpViewModel
import com.hoanglong180903.shoppingease.viewmodel.SignUpViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var repository: SignUpRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        requestRegisterUser()
    }

    private fun init(){
        repository = SignUpRepository()
        viewModel = ViewModelProvider(this,SignUpViewModelFactory(application))[SignUpViewModel::class.java]
    }

    private fun requestRegisterUser(){
        binding.btnSignUp.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val loc = listOf(105.9104621, 21.1358758)
            val user = User("",binding.edName.text.toString(),"",binding.edPassword.text.toString(),binding.edEmail.text.toString(),"","No image",null)
//            CoroutineScope(Dispatchers.IO).launch {
//                viewModel.register(user)
//            }

            viewModel.register(user).observe(this){
                it?.let { resources ->
                    when (resources.status) {
                        Status.SUCCESS -> {
                            Toast.makeText(applicationContext,"Đăng ký thành công",Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }

                        Status.ERROR -> {
                            Toast.makeText(applicationContext,"Đăng ký thất bại" + it.message,Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }

                        Status.LOADING -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
//        viewModel.errormsg.observe(this){
//            Log.e("sign_up",it.toString())
//        }
//        viewModel.loading.observe(this){
//            if (it){
//                binding.progressBar.visibility = View.VISIBLE
//            }else{
//                binding.progressBar.visibility = View.GONE
//            }
//        }
//        viewModel.isSuccess.observe(this){
//            if (it){
//                Toast.makeText(this,"Dang ky khong thanh cong",Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(this,"Dang ky thanh cong",Toast.LENGTH_SHORT).show()
//            }
//        }

    }
}