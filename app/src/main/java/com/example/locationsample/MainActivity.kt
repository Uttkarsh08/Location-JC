package com.example.locationsample

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationsample.ui.theme.LocationSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    myApp(viewModel)
                }
            }
        }
    }
}
//7 --> LocationData
@Composable
fun myApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val LocationUtils = LocationUtils(context)
    LocationDisplay(context = context, viewModel, locationUtils = LocationUtils)
}




//3
@Composable
fun LocationDisplay(context: Context,
                    viewModel: LocationViewModel,  //10 --> Utils
                    locationUtils: LocationUtils){
    
/*14*/    val location = viewModel.location.value   // updated value of the location derived from the viewModel to be used in the composable
/*17*/    val address = location?.let {
        locationUtils.reverseGeoCodeLocation(location)
    }
//5
    val requestPermissionlaucher = rememberLauncherForActivityResult(    // to display the popup for requesting the user to grant the permission
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true){
                    // Have access to location
//13                //13
                locationUtils.requestLocationUpdates(viewModel)  // request the location update from the user
            }else{
                //Ask for permission
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(  // display why we need the permission to the user
                    context as MainActivity,  // because we want to display the launcher inside the mainactivity only(not on different screen)
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ||  ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION)

                if(rationalRequired){   //reationalRequired hold the info that if we want to display laucher or not.
                    Toast.makeText(context, "Location Permission is required for this feature to work",  //Toast is used to display any message on the screen as a popup
                        Toast.LENGTH_LONG).show()   // Length_Long for the duration for which the popup is displayed on the screen
                }else{
                    Toast.makeText(context,
                        "Location Permission is required, Please enable it in Android Settings",
                        Toast.LENGTH_LONG).show()
                }

            }
        }
    )
//4 (till last)
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

/*15 --> Utils*/        if (location != null){
            Text(text = "Address: ${location.latitude} ${location.longitude} \n $address",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
        }else{
            Text(text = "Location not Found")
        }


        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
                // 13  Location already granted and now have to update the location
                locationUtils.requestLocationUpdates(viewModel)
            }else{
//6                //6 (Request the location), coded after making permissionlauncher.
                requestPermissionlaucher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
}
