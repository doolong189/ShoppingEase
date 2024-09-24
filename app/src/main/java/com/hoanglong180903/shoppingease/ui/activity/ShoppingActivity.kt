package com.hoanglong180903.shoppingease.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.hoanglong180903.shoppingease.MainActivity
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.adapter.CategoryAdapter
import com.hoanglong180903.shoppingease.adapter.ProductAdapter
import com.hoanglong180903.shoppingease.databinding.ActivityShoppingBinding
import com.hoanglong180903.shoppingease.listener.OnClickItemCategory
import com.hoanglong180903.shoppingease.listener.OnClickItemProduct
import com.hoanglong180903.shoppingease.model.LocationResponse
import com.hoanglong180903.shoppingease.utils.CustomGoogleMap
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.utils.Status
import com.hoanglong180903.shoppingease.viewmodel.CategoryViewModel
import com.hoanglong180903.shoppingease.viewmodel.ProductViewModel
import com.hoanglong180903.shoppingease.viewmodel.UserViewModel


class ShoppingActivity : AppCompatActivity(), OnClickItemProduct , OnMapReadyCallback,
    GoogleMap.OnMapClickListener , OnClickItemCategory{
    private lateinit var binding : ActivityShoppingBinding
    private lateinit var viewModel : ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var userViewModel : UserViewModel
    private lateinit var adapter : ProductAdapter
    private lateinit var categoryAdapter : CategoryAdapter
    private lateinit var mySharedPreferences: MySharedPreferences
    private var mMap : GoogleMap? = null
    private var list_spinner_radius : List<Int> = listOf(1,2,5,10,20,40,80,100,250,500)
    private var idSpinnerRadius : Int = 0
    private val options = GoogleMapOptions()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var currentPolygon: Polygon? = null
    private lateinit var point : LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setUpRecyclerview()
//        getLocation()
        changeLocation( )
    }

    private fun init(){
        viewModel = ViewModelProvider(this, ProductViewModel.ProductViewModelFactory(application))[ProductViewModel::class.java]
        categoryViewModel = ViewModelProvider(this,CategoryViewModel.CategoryViewModelFactory(application))[CategoryViewModel::class.java]
        userViewModel = ViewModelProvider(this,UserViewModel.UserViewModelFactory(application))[UserViewModel::class.java]
        adapter = ProductAdapter(applicationContext,this)
        categoryAdapter = CategoryAdapter(applicationContext,this)
        mySharedPreferences = MySharedPreferences(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //init gg map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.shopping_mapView) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //toolbar
        setSupportActionBar(binding.shoppingToolBar)
        title = "Màn hình chính"
    }

    private fun setUpRecyclerview(){
        binding.rcProduct.setHasFixedSize(true)
        binding.rcProduct.layoutManager = GridLayoutManager(applicationContext,2)
        binding.rcProduct.adapter = adapter

        binding.rcCategory.setHasFixedSize(true)
//        binding.rcCategory.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
        binding.rcCategory.layoutManager = GridLayoutManager(applicationContext,4)
        binding.rcCategory.adapter = categoryAdapter

        binding.swipeLayout.setOnRefreshListener {
            getData()
        }
        getData()
    }

    private fun getData(){
        viewModel.getProduct(mySharedPreferences.userId.toString()).observe(this) { it ->
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        binding.swipeLayout.isRefreshing = false
                        viewModel.deleteAllProductToDatabase()
                        resources.data?.let {product ->
                            adapter.setProducts(product)
                            viewModel.insertAllProductToDatabase(product)
                            binding.shoppingTvEmpty.visibility = View.GONE
                        }
                    }

                    Status.ERROR -> {
                        binding.swipeLayout.isRefreshing = false
                        Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                        Log.e("data_shopping",it.message.toString())
                        //need get data from room database
                        getListFromDatabase()
                    }

                    Status.LOADING -> {
                        binding.swipeLayout.isRefreshing = true
                    }
                }
            }
        }

        categoryViewModel.getCategory().observe(this){
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        binding.swipeLayout.isRefreshing = false
                        resources.data?.let {product ->
                            categoryAdapter.setBrands(product)
                        }
                    }

                    Status.ERROR -> {
                        binding.swipeLayout.isRefreshing = false
                        Log.e("data_category_shopping",it.message.toString())
                    }

                    Status.LOADING -> {
                        binding.swipeLayout.isRefreshing = true
                    }
                }
            }
        }
    }

    private fun getListFromDatabase(){
        viewModel.getAllProduct().observe(this){
            Toast.makeText(this,"Get database from room",Toast.LENGTH_SHORT).show()
            adapter.setProducts(it)
        }
    }


    override fun onClickItem(id: String) {
        val intent = Intent(this,DetailProductActivity::class.java)
        intent.putExtra("idDetailProduct",id)
        startActivity(intent)
    }

    private fun changeLocation(){
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_spinner_radius)
        binding.shoppingSpRadius.adapter = spAdapter
        val LA_LACTION = LatLng(mySharedPreferences.getUserLoc()!![1],mySharedPreferences.getUserLoc()!![0])
        binding.shoppingSpRadius.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View, i: Int, l: Long
                ) {
                    idSpinnerRadius = list_spinner_radius[i]
                    currentPolygon?.remove()
                    currentPolygon = mMap!!.addPolygon(
                        CustomGoogleMap.createPolygonWithCircle(
                            applicationContext,
                            LA_LACTION,
                            idSpinnerRadius
                        )
                    )
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap!!.setMinZoomPreference(11.0f)
        mMap!!.setMaxZoomPreference(15.0f)
        val point = LatLng(mySharedPreferences.getUserLoc()!![1],mySharedPreferences.getUserLoc()!![0])
        mMap!!.addMarker(MarkerOptions().position(point).title(""))
//        mMap!!.setOnMapClickListener(this)
        options.mapType(GoogleMap.MAP_TYPE_NONE)
            .compassEnabled(false)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(false)
        findLocationProduct(point)
    }

    override fun onMapClick(point: LatLng) {
        mMap!!.clear()
        mMap!!.addMarker(MarkerOptions().position(point).title(""))
        mMap!!.clear()
        currentPolygon?.remove()
        currentPolygon = mMap!!.addPolygon(CustomGoogleMap.createPolygonWithCircle(applicationContext, point, idSpinnerRadius))
    }

    private fun findLocationProduct(point : LatLng){
        val loc = listOf(point.longitude,point.latitude)
        binding.shoppingBtnSave.setOnClickListener {
            val location = LocationResponse(loc,idSpinnerRadius,mySharedPreferences.userId.toString())
            viewModel.foundLocationProduct(location).observe(this){
                it?.let { resources ->
                    when (resources.status) {
                        Status.SUCCESS -> {
                            binding.swipeLayout.isRefreshing = false
                            resources.data?.let {product ->
                                adapter.setProducts(product)
                            }
                            binding.shoppingTvEmpty.visibility = View.GONE
                        }
                        Status.ERROR -> {
                            binding.swipeLayout.isRefreshing = false
                            adapter.setProducts(emptyList())
                            binding.shoppingTvEmpty.visibility = View.VISIBLE
                        }
                        Status.LOADING -> {
                            binding.swipeLayout.isRefreshing = true
                        }
                    }
                }
            }
        }
    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                7979
            )
        } else {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location  ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    point  = LatLng(latitude,longitude)
                    currentPolygon?.remove()
                    currentPolygon = mMap!!.addPolygon(CustomGoogleMap.createPolygonWithCircle(applicationContext, point, idSpinnerRadius))
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap!!.isMyLocationEnabled = true
                    }
                } else {
                    Log.e("location_current","Không lấy được vị trí hiện tại")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 7979) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Từ chối", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.button_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.btn_add -> {
                val intent = Intent(this,CreateActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_chat -> {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_exit -> {
                mySharedPreferences.clearUserData()
                val intent = Intent(this,SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClickItemCategory(id: String) {
        viewModel.getProductWithCategory(mySharedPreferences.userId.toString(),id).observe(this){
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        binding.swipeLayout.isRefreshing = false
                        viewModel.deleteAllProductToDatabase()
                        resources.data?.let {product ->
                            adapter.setProducts(product)
                            viewModel.insertAllProductToDatabase(product)
                            binding.shoppingTvEmpty.visibility = View.GONE
                        }
                        binding.shoppingTvEmpty.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        binding.swipeLayout.isRefreshing = false
                        Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                        Log.e("data_shopping",it.message.toString())
                        //need get data from room database
                        getListFromDatabase()
                    }

                    Status.LOADING -> {
                        binding.swipeLayout.isRefreshing = true
                    }
                }
            }
        }
    }
}