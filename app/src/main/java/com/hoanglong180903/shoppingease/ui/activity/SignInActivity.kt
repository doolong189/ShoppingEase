package com.hoanglong180903.shoppingease.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hoanglong180903.shoppingease.MainActivity
import com.hoanglong180903.shoppingease.databinding.ActivitySignInBinding
import com.hoanglong180903.shoppingease.repository.SignInRepository
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.viewmodel.SignInViewModel
import com.hoanglong180903.shoppingease.viewmodel.SignInViewModelFactory

class SignInActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding
    private lateinit var viewModel : SignInViewModel
    private lateinit var repository: SignInRepository
    private lateinit var sharedPreferences: MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        login()
        goToSignUp()
    }

    private fun init(){
        repository = SignInRepository()
        viewModel = ViewModelProvider(this, SignInViewModelFactory())[SignInViewModel::class.java]
        sharedPreferences = MySharedPreferences(this)
    }
    private fun login(){
        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.login(binding.edEmail.text.toString(),binding.edPassword.text.toString())
        }

        viewModel.loading.observe(this){
            if (it){
                binding.progressBar.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.VISIBLE
            }
        }
        viewModel.mUserLogin.observe(this){
            Toast.makeText(this,it.successMessage, Toast.LENGTH_SHORT).show()
            sharedPreferences.saveUserData(it.user!!._id,it.user!!.name,it.user!!.email,it.user!!.password,it.user!!.phone,it.user!!.address,it.user!!.image, it.user!!.loc)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewModel.isSuccess.observe(this){
            if (it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun goToSignUp(){
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}