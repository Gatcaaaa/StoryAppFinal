package com.submisson.aleggappstory.view.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.submisson.aleggappstory.R
import com.submisson.aleggappstory.data.Result
import com.submisson.aleggappstory.databinding.ActivityMapsBinding
import com.submisson.aleggappstory.view.ViewModelFactory
import com.submisson.aleggappstory.view.welcome.WelcomeActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        setupMaps()
        setupMapsMarker()
    }

    private fun setupMaps() {
        mapsViewModel.getSession().observe(this){ user ->
            if (!user.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                mapsViewModel.getStoriesWithLocation(user.token)
            }
        }
    }

    private fun setupMapsMarker() {
        mapsViewModel.mapsLocationViewModel.observe(this){
            when (it){
                is  Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val builder = LatLngBounds.Builder()
                    it.data.forEach { data ->
                        if (data.lat != null && data.lon != null){
                            val latLng = LatLng(data.lat, data.lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(data.name)
                                    .snippet(data.description)
                            )
                            builder.include(latLng)
                        }
                    }
                    val bounds: LatLngBounds = builder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                is Result.Error -> {
                    showLoading(false)
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}