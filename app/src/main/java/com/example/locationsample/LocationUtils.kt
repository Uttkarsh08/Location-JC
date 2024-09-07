package com.example.locationsample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
//1
class LocationUtils(val context: Context) {  // Context carries essential details about the application,
    // such as its current state, resources, and the application itself.
    //11
    private val _fusedLocationClient: FusedLocationProviderClient  // used to get the latitude and longitude of the user
    = LocationServices.getFusedLocationProviderClient(context)

//12 --> MainActivity
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: LocationViewModel){
        val locationCallback = object : LocationCallback(){  // creating an object of LocationCallback to ovveride its function
            // which contains the location latitude and longitude) of the user
            override fun onLocationResult(LocationResult: LocationResult) {
                super.onLocationResult(LocationResult)
                LocationResult.lastLocation?.let {   // get the last location of the user using the overriden object
                    // and update it inside the LocationData, let is used to unpack the Locationresult.
                    val Location = LocationData(
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                    viewModel.updateLocation(Location)  //  update the location to the current one
                }
            }
        }
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        _fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

//2->MainActivity
    fun hasLocationPermission(context: Context): Boolean{ // A helper function which can
    // be used anywhere to check if we have location permission or not

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
//16 --> MainActivity
    fun reverseGeoCodeLocation(location: LocationData): String{   // Convert latitude and longitude to a readable address
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(location.latitude, location.longitude)
        val addresses:MutableList<Address>? = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)

        return if (addresses?.isNotEmpty() == true){
            addresses[0].getAddressLine(0)
        }else {
            "Address not Found"
        }

    }

}