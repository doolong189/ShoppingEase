package com.hoanglong180903.shoppingease.ui.activity

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hoanglong180903.shoppingease.databinding.ActivityCreateBinding
import com.hoanglong180903.shoppingease.model.Brand
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.utils.Status
import com.hoanglong180903.shoppingease.viewmodel.CategoryViewModel
import com.hoanglong180903.shoppingease.viewmodel.ProductViewModel
import java.util.Calendar
import java.util.Date

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var selectedImage: Uri? = null
    private var strImageUpload: String = ""
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var product: Product
    private var idCategory: String = ""
    private lateinit var mySharedPreferences: MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        addData()
        actionUploadImage()
    }

    private fun init() {
        viewModel = ViewModelProvider(
            this, ProductViewModel.ProductViewModelFactory(application)
        )[ProductViewModel::class.java]
        categoryViewModel = ViewModelProvider(
            this, CategoryViewModel.CategoryViewModelFactory(application)
        )[CategoryViewModel::class.java]
        mySharedPreferences = MySharedPreferences(this)
    }

    private fun actionUploadImage(){
        binding.productImgProduct.setOnClickListener {
            val mIntent = Intent()
            mIntent.type = "image/*"
            mIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(mIntent, 75)
        }
        binding.profileImgCameraProduct.setOnClickListener {
            val mIntent = Intent()
            mIntent.type = "image/*"
            mIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(mIntent, 75)
        }
    }

    private fun addData() {
        binding.productBtnSave.setOnClickListener {
            val name = binding.productEdName.text.toString()
            val price = binding.productEdPrice.text.toString()
            val quality = binding.productEdQuantity.text.toString()
            val description = binding.productEdDescription.text.toString()
            val user: User = User(_id = mySharedPreferences.userId!!, "", "", "", "", "", "",null)
            val brand: Brand = Brand(_id = idCategory, "", "")
            binding.createProgress.visibility = View.VISIBLE
            product = Product(
                name = binding.productEdName.text.toString(),
                price = binding.productEdPrice.text.toString().toDouble(),
                quantity = binding.productEdQuantity.text.toString().toInt(),
                description = binding.productEdDescription.text.toString(),
                image = strImageUpload,
                idUser = user,
                idBrand = brand
            )
            viewModel.addProduct(product).observe(this) {
                it?.let { resources ->
                    when (resources.status) {
                        Status.SUCCESS -> {
                            Toast.makeText(applicationContext, "Tạo thành công", Toast.LENGTH_SHORT)
                                .show()
                            binding.productEdName.setText("")
                            binding.productEdPrice.setText("")
                            binding.productEdQuantity.setText("")
                            binding.productEdDescription.setText("")
                            binding.createProgress.visibility = View.GONE
                            val intent = Intent(this, ShoppingActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        Status.ERROR -> {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT)
                                .show()
                            Log.e("data_shopping", it.message!!)
                            binding.createProgress.visibility = View.GONE
                        }

                        Status.LOADING -> {
                            binding.createProgress.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        binding.productBtnCancel.setOnClickListener {
            val intent = Intent(this, ShoppingActivity::class.java)
            startActivity(intent)
            finish()
        }

        getSpinnerCategoryFromApi()
    }

    private fun getSpinnerCategoryFromApi() {
        categoryViewModel.getCategory().observe(this) {
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        resources.data?.let { category ->
                            val spAdapter = ArrayAdapter(
                                this, R.layout.simple_spinner_item, category
                            )
                            binding.productSpCategory.adapter = spAdapter
                            binding.productSpCategory.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?, view: View, i: Int, l: Long
                                    ) {
                                        idCategory = category[i]._id
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                    }
                                }
                        }
                    }

                    Status.ERROR -> {
                        Toast.makeText(
                            applicationContext, "Error get data from api", Toast.LENGTH_SHORT
                        ).show()
                    }

                    Status.LOADING -> {
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.createProgressImage.visibility = View.VISIBLE
                val storage = FirebaseStorage.getInstance()
                val date = Date()
                val reference = storage.reference.child("Creates").child(date.time.toString() + "")
                reference.putFile(data.data!!)
                    .addOnSuccessListener {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            strImageUpload = uri.toString()
                            binding.createProgressImage.visibility = View.GONE
                            binding.productImgProduct.setImageURI(data.data!!)
                            Log.d("Upload", "Success: $strImageUpload")
                        }
                    }
                    .addOnFailureListener { exception ->
                        binding.createProgressImage.visibility = View.GONE
                        strImageUpload = "No image"
                        Log.e("Upload", "Failure", exception)
                    }
            }
        }
    }
}