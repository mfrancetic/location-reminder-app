package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.*
import org.koin.android.ext.android.inject
import org.koin.core.logger.KOIN_TAG

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient

    private lateinit var fragmentContext: Context

    companion object {
        const val GEOFENCE_RADIUS_IN_METERS = 100f
        const val ACTION_GEOFENCE_EVENT =
            "LocationReminderApp.action.ACTION_GEOFENCE_EVENT"
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.selectLocation.setOnClickListener {
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        fragmentContext = binding.reminderTitle.context
        geofencingClient = LocationServices.getGeofencingClient(fragmentContext)

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            val reminderDTO = _viewModel.validateAndSaveReminder(
                ReminderDataItem(
                    title,
                    description,
                    location,
                    latitude,
                    longitude
                )
            )
            if (reminderDTO != null) {
                addAGeofence(reminderDTO)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsAndEnableGeofencing()
    }

    private fun checkPermissionsAndEnableGeofencing() {
        if (areforegroundAndBackgroundLocationPermissionApproved(fragmentContext)) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationSettingsResponseTask = getLocationSettingsResponseTask(fragmentContext)

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(KOIN_TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                println(getString(R.string.location_enabled))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (isForegroundAndBackgroundPermissionResultMissing(grantResults, requestCode)) {
            Snackbar.make(
                requireView(),
                R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    // Displays App settings screen.
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (areforegroundAndBackgroundLocationPermissionApproved(fragmentContext))
            return

        val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = getForegroundAndBackgroundResultCode(permissionsArray)

        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    @SuppressLint("MissingPermission")
    private fun addAGeofence(reminderDTO: ReminderDTO) {
        val geofence = Geofence.Builder()
            .setRequestId(reminderDTO.id)
            .setCircularRegion(
                reminderDTO.latitude!!, reminderDTO.longitude!!, GEOFENCE_RADIUS_IN_METERS
            )
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if (areforegroundAndBackgroundLocationPermissionApproved(fragmentContext)) {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.i(KOIN_TAG, getString(R.string.geofence_added))
                }
                addOnFailureListener {
                    Log.i(KOIN_TAG, getString(R.string.error_adding_geofence))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.onClear()
    }
}