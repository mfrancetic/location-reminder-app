package com.udacity.project4.utils

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

val runningQOrLater =
    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
const val LOCATION_PERMISSION_INDEX = 0
const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
const val REQUEST_LOCATION_PERMISSION = 2

@TargetApi(29)
fun areforegroundAndBackgroundLocationPermissionApproved(fragmentContext: Context): Boolean {
    val foregroundLocationApproved = (
            PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(
                        fragmentContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ))
    val backgroundPermissionApproved =
        if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(
                        fragmentContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
        } else {
            true
        }
    return foregroundLocationApproved && backgroundPermissionApproved
}

fun areLocationServicesEnabled(fragmentContext: Context): Boolean {
    val locationManager =
        fragmentContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

@TargetApi(29)
fun getForegroundAndBackgroundResultCode(permissionsArray: Array<String>): Int {
    var newPermissionsArray = permissionsArray
    return when {
        runningQOrLater -> {
            newPermissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
            REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
        }
        else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
    }
}

fun isForegroundAndBackgroundPermissionResultMissing(
    grantResults: IntArray,
    requestCode: Int
): Boolean {
    return grantResults.isEmpty() || grantResults.size < 2 ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
}

fun getLocationSettingsResponseTask(fragmentContext: Context): Task<LocationSettingsResponse> {
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_LOW_POWER
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val settingsClient = LocationServices.getSettingsClient(fragmentContext)

    return settingsClient.checkLocationSettings(builder.build())
}

fun isForegroundLocationPermissionGrantedFromResult(grantResults: IntArray): Boolean {
    return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
}

fun isRequestCodeEqualLocationPermissionCode(requestCode: Int): Boolean {
    return requestCode == REQUEST_LOCATION_PERMISSION
}

fun isRequestCodeEqualTurnDeviceLocationOnCode(requestCode: Int): Boolean {
    return requestCode == REQUEST_TURN_DEVICE_LOCATION_ON
}

fun isForegroundLocationGrantedFromContext(fragmentContext: Context): Boolean{
   return ContextCompat.checkSelfPermission(
        fragmentContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}