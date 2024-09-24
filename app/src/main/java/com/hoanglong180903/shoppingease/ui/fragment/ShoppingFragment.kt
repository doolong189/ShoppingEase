package com.hoanglong180903.shoppingease.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.adapter.CategoryAdapter
import com.hoanglong180903.shoppingease.adapter.ProductAdapter
import com.hoanglong180903.shoppingease.databinding.FragmentShoppingBinding
import com.hoanglong180903.shoppingease.listener.OnClickItemCategory
import com.hoanglong180903.shoppingease.listener.OnClickItemProduct
import com.hoanglong180903.shoppingease.model.LocationResponse
import com.hoanglong180903.shoppingease.ui.activity.DetailProductActivity
import com.hoanglong180903.shoppingease.utils.CustomGoogleMap
import com.hoanglong180903.shoppingease.utils.MySharedPreferences
import com.hoanglong180903.shoppingease.utils.Status
import com.hoanglong180903.shoppingease.viewmodel.CategoryViewModel
import com.hoanglong180903.shoppingease.viewmodel.ProductViewModel
import com.hoanglong180903.shoppingease.viewmodel.UserViewModel


class ShoppingFragment : Fragment(), OnClickItemProduct, OnClickItemCategory {
    private lateinit var binding: FragmentShoppingBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var mySharedPreferences: MySharedPreferences
//    private var mMap: GoogleMap? = null
    private lateinit var mMap: GoogleMap

    private var list_spinner_radius: List<Int> = listOf(1, 2, 5, 10, 20, 40, 80, 100, 250, 500)
    private var idSpinnerRadius: Int = 0
    private val options = GoogleMapOptions()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var currentPolygon: Polygon? = null
    private lateinit var point: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShoppingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setUpRecyclerview()
        binding.shoppingTvChangeLocation.setOnClickListener {
            dialogChangeLocation()
        }
    }

    private fun init() {
        viewModel = ViewModelProvider(
            this,
            ProductViewModel.ProductViewModelFactory(requireActivity().application)
        )[ProductViewModel::class.java]
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModel.CategoryViewModelFactory(requireActivity().application)
        )[CategoryViewModel::class.java]
        userViewModel = ViewModelProvider(
            this,
            UserViewModel.UserViewModelFactory(requireActivity().application)
        )[UserViewModel::class.java]
        adapter = ProductAdapter(requireContext(), this)
        categoryAdapter = CategoryAdapter(requireContext(), this)
        mySharedPreferences = MySharedPreferences(requireContext())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun setUpRecyclerview() {
        binding.rcProduct.setHasFixedSize(true)
        binding.rcProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcProduct.adapter = adapter

        binding.rcCategory.setHasFixedSize(true)
        binding.rcCategory.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rcCategory.adapter = categoryAdapter

        binding.swipeLayout.setOnRefreshListener {
            getData()
        }
        getData()
    }

    private fun getData() {
        viewModel.getProduct(mySharedPreferences.userId.toString())
            .observe(viewLifecycleOwner) { it ->
                it?.let { resources ->
                    when (resources.status) {
                        Status.SUCCESS -> {
                            binding.swipeLayout.isRefreshing = false
                            viewModel.deleteAllProductToDatabase()
                            resources.data?.let { product ->
                                adapter.setProducts(product)
                                viewModel.insertAllProductToDatabase(product)
                                binding.shoppingTvEmpty.visibility = View.GONE
                            }
                        }

                        Status.ERROR -> {
                            binding.swipeLayout.isRefreshing = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            Log.e("data_shopping", it.message.toString())
                            //need get data from room database
                            getListFromDatabase()
                        }

                        Status.LOADING -> {
                            binding.swipeLayout.isRefreshing = true
                        }
                    }
                }
            }

        categoryViewModel.getCategory().observe(viewLifecycleOwner) {
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        binding.swipeLayout.isRefreshing = false
                        resources.data?.let { product ->
                            categoryAdapter.setBrands(product)
                        }
                    }

                    Status.ERROR -> {
                        binding.swipeLayout.isRefreshing = false
                        Log.e("data_category_shopping", it.message.toString())
                    }

                    Status.LOADING -> {
                        binding.swipeLayout.isRefreshing = true
                    }
                }
            }
        }
    }

    private fun getListFromDatabase() {
        viewModel.getAllProduct().observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Get database from room", Toast.LENGTH_SHORT).show()
            adapter.setProducts(it)
        }
    }

    override fun onClickItem(id: String) {
        val intent = Intent(requireContext(), DetailProductActivity::class.java)
        intent.putExtra("idDetailProduct", id)
        startActivity(intent)
    }

    override fun onClickItemCategory(id: String) {
        viewModel.getProductWithCategory(mySharedPreferences.userId.toString(), id).observe(this) {
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        binding.swipeLayout.isRefreshing = false
                        viewModel.deleteAllProductToDatabase()
                        resources.data?.let { product ->
                            adapter.setProducts(product)
                            viewModel.insertAllProductToDatabase(product)
                            binding.shoppingTvEmpty.visibility = View.GONE
                        }
                    }

                    Status.ERROR -> {
                        binding.swipeLayout.isRefreshing = false
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.e("data_shopping", it.message.toString())
                        binding.shoppingTvEmpty.visibility = View.VISIBLE
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

    private fun dialogChangeLocation() {
        val dialog = Dialog(requireActivity())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Make map clear
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setContentView(R.layout.dialog_change_location)
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes
        val mMapView = dialog.findViewById<MapView>(R.id.mapView)
        val shoppingSpinner = dialog.findViewById<Spinner>(R.id.dialog_shopping_spRadius)
        MapsInitializer.initialize(requireActivity())

        mMapView.onCreate(dialog.onSaveInstanceState())
        mMapView.onResume()
        val position = LatLng(mySharedPreferences.getUserLoc()!![1], mySharedPreferences.getUserLoc()!![0])

        //
        mMapView.getMapAsync { googleMap ->
            mMap = googleMap
            googleMap.addMarker(MarkerOptions().position(position).title("Your title"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(position))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12f), 2000, null)
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        //spinner
        val spAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            list_spinner_radius
        )
        shoppingSpinner.adapter = spAdapter
        shoppingSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View, i: Int, l: Long
                ) {
                    idSpinnerRadius = list_spinner_radius[i]
                    if (::mMap.isInitialized) {
                        currentPolygon?.remove()
                        currentPolygon = mMap.addPolygon(
                            CustomGoogleMap.createPolygonWithCircle(requireContext(), position, idSpinnerRadius)
                        )
                    } else {
                        Log.e("MapError", "GoogleMap is not ready yet!")
                    }
                }
                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        //button
        val dialogButton = dialog.findViewById<Button>(R.id.dialog_shopping_btnSave)
        dialogButton.setOnClickListener {
            findLocationProduct(idSpinnerRadius)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun findLocationProduct(position : Int){
        val loc = listOf( mySharedPreferences.getUserLoc()!![0],mySharedPreferences.getUserLoc()!![1])
        val location = LocationResponse(loc,idSpinnerRadius,mySharedPreferences.userId.toString())
        Log.e("zzz",position.toString())
        viewModel.foundLocationProduct(location).observe(viewLifecycleOwner){
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