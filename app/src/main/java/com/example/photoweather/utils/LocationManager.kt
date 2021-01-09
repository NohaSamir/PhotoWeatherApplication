package com.example.photoweather.utils

import android.Manifest
import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*


class LocationManager constructor(
    private val activity: Activity,
    private val locationManagerInteraction: LocationManagerInteraction
) {

    companion object Constants {
        const val TAG = "LocationManager"

        /**
         * Used to prompt location settings dialog
         */
        const val REQUEST_CHECK_SETTINGS = 2

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        //private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 300000; // 5 mints
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000 // 5 mints


        /**
         * The fastest rate for active location updates. Exact. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2

    }

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private lateinit var mLocationRequest: LocationRequest

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    /**
     * Callback for Location events.
     */
    private var mLocationCallback: LocationCallback? = null


    init {
        setupLocationService()
    }

    private fun setupLocationService() {
        // init location clients.
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity.applicationContext)
        mSettingsClient = LocationServices.getSettingsClient(activity.applicationContext)
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                locationManagerInteraction.onLocationRetrieved(location, getAddress(location))
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Uses a [LocationSettingsRequest.Builder] to build
     * a [LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)
            ?.addOnSuccessListener(activity) {
                Log.d(TAG, "All location settings are satisfied.")
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Check permission but I already check permission before use this class
                    return@addOnSuccessListener
                }
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )
            }
            ?.addOnFailureListener(activity) { e ->
                val statusCode: Int = (e as ApiException).statusCode
                handleStartLocationFailureCases(e as ResolvableApiException, statusCode)
            }
    }

    private fun handleStartLocationFailureCases(e: ResolvableApiException, statusCode: Int) {
        when (statusCode) {
            RESOLUTION_REQUIRED -> {
                Log.d(
                    TAG, "Location settings are not satisfied. Attempting to upgrade " +
                            "location settings "
                )
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the
                    // result in onActivityResult().
                    e.startResolutionForResult(
                        activity, REQUEST_CHECK_SETTINGS
                    )
                } catch (sie: SendIntentException) {
                    Log.d(TAG, "PendingIntent unable to execute request.")
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                val errorMessage = "Location settings are inadequate, and cannot be " +
                        "fixed here. Fix in Settings."
                Log.e(TAG, errorMessage)
            }
            else -> {
            }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    fun stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        mFusedLocationClient = null
        mLocationCallback = null

    }

    fun getAddress(location: Location): String {
        var address = ""
        val gcd = Geocoder(activity.baseContext, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(
                location.getLatitude(),
                location.getLongitude(), 1
            )
            if (addresses.isNotEmpty()) {
                address = addresses[0].locality
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
}