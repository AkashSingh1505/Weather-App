package com.example.synamedia_assignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.synamedia_assignment.BuildConfig
import com.example.synamedia_assignment.R
import com.example.synamedia_assignment.databinding.ActivityMainBinding
import com.example.synamedia_assignment.factory.GenericVMFactory
import com.example.synamedia_assignment.viewmodels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val KEY_CITY = "city"
    private val KEY_DESC = "description"
    private val KEY_TEMP = "temperature"
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var genericVMFactory: GenericVMFactory
    lateinit var weatherViewModel: WeatherViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        pref = this.getPreferences(Context.MODE_PRIVATE)
        editor = pref.edit()

        (this.applicationContext as MyApp).applicationComponent.injectMainActivity(this)
        weatherViewModel = ViewModelProvider(this, genericVMFactory).get(WeatherViewModel::class.java)

        checkAndFetchWeather()

        binding.refreshButton.setOnClickListener {
            checkAndFetchWeather()
        }
    }

    private fun checkAndFetchWeather() {
        if (isNetworkAvailable(this)) {
            checkAndRequestLocationPermission()
        } else {
            updateViews()
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateViews() {
        binding.apply {
            locationTextView.text = pref.getString(KEY_CITY, "Unknown")
            weatherDescriptionTextView.text = pref.getString(KEY_DESC, "Unknown")
            temperatureTextView.text = "${pref.getString(KEY_TEMP, "Unknown")} K"

            // Load the weather icon
            val iconFileName = "${pref.getString(KEY_DESC, "unknown")}.png"
            val iconFile = File(cacheDir, iconFileName)
            if (iconFile.exists()) {
                Glide.with(this@MainActivity)
                    .load(iconFile)
                    .into(weatherIconImageView)
            } else {
                // Load default or placeholder image
                Glide.with(this@MainActivity)
                    .load(R.drawable.baseline_image_24) // Replace with your placeholder image resource
                    .into(weatherIconImageView)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkAndRequestLocationPermission() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            coarseLocationPermission != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            getLocationCoordinates()
        }
    }

    private fun requestLocationPermission() {
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            getLocationCoordinates()
        } else {
            Toast.makeText(this, "Location permissions are required to use this feature.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationCoordinates() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val cityName = getCityName(location.latitude, location.longitude)
                val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY
                weatherViewModel.getCurrentWeather(cityName,apiKey)
                lifecycleScope.launch {
                    weatherViewModel.weatherResponse.collect { weatherResponse ->
                        weatherResponse?.let {
                            editor.apply {
                                putString(KEY_CITY, it.name)
                                putString(KEY_DESC, it.weather.firstOrNull()?.description)
                                putString(KEY_TEMP, it.main.temp.toString())
                            }.apply()

                            val iconUrl = "https://openweathermap.org/img/wn/${it.weather.firstOrNull()?.icon}.png"
                            downloadAndSaveIcon(iconUrl, "${it.weather.firstOrNull()?.description}.png")

                            updateViews()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Unable to retrieve location. Try again later.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCityName(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return geocoder.getFromLocation(lat, lon, 1)
            ?.firstOrNull()?.locality ?: "Unknown"
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    private suspend fun downloadAndSaveIcon(iconUrl: String, fileName: String) {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(iconUrl).build()
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val file = File(cacheDir, fileName)
                        FileOutputStream(file).use { fos ->
                            fos.write(response.body?.bytes())
                        }
                    } else {
                        Log.e("MainActivity", "Failed to download icon: ${response.message}")
                    }
                }
            } catch (e: IOException) {
                Log.e("MainActivity", "Error downloading icon: ${e.message}")
            }
        }
    }
}
