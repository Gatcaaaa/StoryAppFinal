package com.submisson.aleggappstory.view.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.submisson.aleggappstory.R
import com.submisson.aleggappstory.data.Result
import com.submisson.aleggappstory.data.response.UploadStoriesResponse
import com.submisson.aleggappstory.data.retrofit.ApiConfig
import com.submisson.aleggappstory.databinding.ActivityAddStoryBinding
import com.submisson.aleggappstory.view.ViewModelFactory
import com.submisson.aleggappstory.view.getImageUri
import com.submisson.aleggappstory.view.main.MainActivity
import com.submisson.aleggappstory.view.reduceFileImage
import com.submisson.aleggappstory.view.uriTofile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private  var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permission ->
            when {
                permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    lastLocation()
                }
                permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    lastLocation()
                }
                 else -> {
                     binding.switchLocation.isChecked = false
                 }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

        setupUpload()

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.cameraButton.setOnClickListener {
            startCamera()
        }
        binding.uploadButton.setOnClickListener {
            startUpload()
        }
        binding.switchLocation.setOnCheckedChangeListener{_: CompoundButton?, isChecked: Boolean ->
            if (isChecked){
                if (!isGPSEnabled()) {
                    showEnableGPSDialog()
                }
                lifecycleScope.launch {
                    lastLocation()
                }
            } else {
                currentLocation = null
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.gps_dialog))
            setMessage(getString(R.string.gps_message))
            setPositiveButton(getString(R.string.positive_button)) {_, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            create()
            show()
        }
    }

    private fun setupUpload() {
        addStoryViewModel.addStoryViewModel.observe(this){
            when (it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is  Result.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage(getString(R.string.upload_messege))
                        setCancelable(false)
                        setPositiveButton(getString(R.string.positive_button)) {_, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun startUpload() {
        var token: String
        currentImageUri?.let {uri ->
            val imageFile = uriTofile(uri, this).reduceFileImage()
            val description = binding.edtDescription.text.toString()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            addStoryViewModel.getSession().observe(this){user ->
                token = user.token
                addStoryViewModel.uploadStory(token,multipartBody,requestBody,currentLocation)
            }

        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){isSuccess ->
        if (isSuccess){
            showImage()
        }
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    @Suppress("MissingPermission")
    private fun lastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)&&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null){
                    currentLocation = location
                } else {
                    Toast.makeText(
                        this,
                        R.string.location_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    newLocation()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun newLocation() {
        Toast.makeText(this.baseContext,"GNew Location", Toast.LENGTH_SHORT).show()
        val reqLocation = LocationRequest()
        reqLocation.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        reqLocation.interval = TimeUnit.SECONDS.toMillis(1)
        reqLocation.fastestInterval = 0
        reqLocation.numUpdates = 1
        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
            )
            Looper.myLooper()?.let {
                fusedLocationClient.requestLocationUpdates(
                    reqLocation,locationCallback,it
                )
            }
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            currentLocation = p0.lastLocation
        }
    }

    private fun checkPermission(accessFineLocation: String): Boolean {
        return  ContextCompat.checkSelfPermission(
            this, accessFineLocation
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "Show Image: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressIndicator.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}