package com.hoanglong180903.shoppingease.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.databinding.ActivityDetailProductBinding
import com.hoanglong180903.shoppingease.utils.CustomGoogleMap
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.utils.Status
import com.hoanglong180903.shoppingease.utils.Utils
import com.hoanglong180903.shoppingease.viewmodel.ProductViewModel
import com.hoanglong180903.shoppingease.viewmodel.firebase.ChatViewModel
import java.util.Date


class DetailProductActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {
    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var mySharedPreferences: MySharedPreferences
    private var mMap: GoogleMap? = null
    private val options = GoogleMapOptions()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        getDataFromBundle()
        getLocation()
    }

    private fun init() {
        viewModel = ViewModelProvider(
            this,
            ProductViewModel.ProductViewModelFactory(application)
        )[ProductViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        mySharedPreferences = MySharedPreferences(this)
        /*
        init google map
         */
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.detail_mapView) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /*
              store data with sharedPreferences
        */
        sharedPreferences = MySharedPreferences(this)
    }

    private fun getDataFromBundle() {
        val bundle = intent.extras
        if (bundle != null) {
            val id = bundle.getString("idDetailProduct")
            getDataFromApi(id.toString())
        }
    }

    private fun getDataFromApi(id: String) {
        viewModel.getDetailProduct(id).observe(this) {
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        resources.data?.let { item ->
                            if (item.image == "No image") {
                                binding.detailImageProduct.setImageResource(R.drawable.ic_launcher_foreground)
                            } else {
                                Glide.with(applicationContext)
                                    .load(item.image)
                                    .into(binding.detailImageProduct)
                            }
                            binding.detailTvNameProduct.text = item.name
                            binding.detailTvPrice.text = "Giá tiền: " + Utils.formatPrice(item.price)  + " đ"
                            binding.detailTvQuantity.text = "Số lượng: " + item.quantity.toString()
                            binding.detailTvDescreption.text =
                                "Mô tả của người bán: \n" + item.description
                            if (item.image == "No image") {
                                binding.detailImageUser.setImageResource(R.drawable.ic_launcher_foreground)
                            } else {
                                Glide.with(applicationContext)
                                    .load(item.idUser!!.image)
                                    .into(binding.detailImageUser)
                            }
                            binding.detailTvNameUser.text = item.idUser!!.name
                            binding.detailTvAddress.text = item.idUser.address
                            requestSendMessage(
                                item.idUser._id,
                                "",
                                mySharedPreferences.userName.toString(),
                                item.image
                            )
                        }
                    }

                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }

                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun requestSendMessage(receiver: String, token: String, name: String, image: String) {
        val date = Date()
        val senderRoom = mySharedPreferences.userId + receiver
        val receiverRoom = receiver + mySharedPreferences.userId
        binding.detailBtnSend.setOnClickListener {
            chatViewModel.sendQuestionProduct(
                binding.detailEdMessage.text.toString(),
                mySharedPreferences.userId!!,
                senderRoom,
                receiverRoom,
                image
            )
            binding.detailEdMessage.setText("")
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val hcm = LatLng(21.138512, 105.912810)
        mMap!!.addMarker(MarkerOptions().position(hcm).title(sharedPreferences.address))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(CustomGoogleMap.LA_LOCATION))
        mMap!!.setMinZoomPreference(13.0f)
        mMap!!.setMaxZoomPreference(20.0f)
        options.mapType(GoogleMap.MAP_TYPE_NONE)
            .compassEnabled(false)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(false)
        mMap!!.setOnMapClickListener(this)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(CustomGoogleMap.LA_LOCATION, 5f))
        mMap!!.addPolygon(
            CustomGoogleMap.createPolygonWithCircle(
                this,
                CustomGoogleMap.LA_LOCATION,
                1
            )
        )
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
            mFusedLocationClient.lastLocation.addOnSuccessListener { _ ->
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@addOnSuccessListener
                }
                mMap!!.isMyLocationEnabled = true
                Log.e("location_current",""+mMap!!.myLocation)
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

    override fun onMapClick(point: LatLng) {
        val hcm = LatLng(21.138512, 105.912810)
        val hn = LatLng(21.138347, 105.912580)
        Log.e("calculator",""+Utils.calculateDistance(hcm,hn))
        Toast.makeText(this, "tapped, point=$point", Toast.LENGTH_SHORT).show()
    }
}