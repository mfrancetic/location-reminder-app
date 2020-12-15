package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.*
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    val _selectLocationViewModel: SelectLocationViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fragmentContext: Context

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _selectLocationViewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        fragmentContext = binding.saveLocationButton.context

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragmentContext)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupObserver()

        return binding.root
    }

    private fun setupObserver() {
        _selectLocationViewModel.onSaveLocationClicked.observe(
            viewLifecycleOwner,
            { isLocationSaved ->
                if (isLocationSaved) {
                    onLocationSelected()
                    _selectLocationViewModel.onSaveLocationDone()
                }
            })
    }

    private fun onLocationSelected() {
        EspressoIdlingResource.wrapEspressoIdlingResource {
            _viewModel.navigationCommand.postValue(NavigationCommand.Back)
            val reminderDTO = _selectLocationViewModel.selectedLocation.value
            if (reminderDTO != null) {
                _viewModel.latitude.value = reminderDTO.latitude
                _viewModel.longitude.value = reminderDTO.longitude
                _viewModel.reminderSelectedLocationStr.value = reminderDTO.location
            }
            val selectedPOI = _selectLocationViewModel.selectedPoi.value
            if (selectedPOI != null) {
                _viewModel.selectedPOI.value = selectedPOI
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))

        enableMyLocation()
    }

    private fun setMapClickListener() {
        map.setOnMapClickListener { location ->
            if (_selectLocationViewModel.selectedLocation.value == null) {
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(location.latitude, location.longitude))
                        .title(fragmentContext.getString(R.string.unknown_location))
                )
                _selectLocationViewModel.setLocation(
                    fragmentContext.getString(R.string.unknown_location),
                    location.latitude,
                    location.longitude,
                    null
                )
            } else {
                _viewModel.showErrorMessage.postValue(fragmentContext.getString(R.string.only_one_location_allowed))
            }
        }
    }

    private fun setPoiClick() {
        map.setOnPoiClickListener { poi ->
            if (_selectLocationViewModel.selectedLocation.value == null) {
                val poiMarker = map.addMarker(
                    MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name)
                )
                poiMarker.showInfoWindow()
                _selectLocationViewModel.setLocation(
                    poi.name,
                    poi.latLng.latitude,
                    poi.latLng.longitude,
                    poi
                )
            } else {
                _viewModel.showErrorMessage.postValue(fragmentContext.getString(R.string.only_one_location_allowed))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isForegroundLocationGrantedFromContext(fragmentContext)) {
            if (areLocationServicesEnabled(fragmentContext)) {
                map.isMyLocationEnabled = true
                zoomToUserLocation()
                setMapClickListener()
                setPoiClick()
            } else {
                promptUserToEnableLocationServices()
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun promptUserToEnableLocationServices() {
        _viewModel.showErrorMessage.postValue(fragmentContext.getString(R.string.location_required_error))
    }

    @SuppressLint("MissingPermission")
    private fun zoomToUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                val zoomLevel = 15f
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isRequestCodeEqualLocationPermissionCode(requestCode)) {
            if (isForegroundLocationPermissionGrantedFromResult(grantResults)) {
                enableMyLocation()
            } else {
                if ((areLocationServicesEnabled(fragmentContext))) {
                    promptUserToGrantLocationPermission()
                } else {
                    promptUserToEnableLocationServices()
                }
            }
        }
    }

    private fun promptUserToGrantLocationPermission() {
        _viewModel.showErrorMessage.postValue(fragmentContext.getString(R.string.permission_denied_explanation))
    }

    override fun onDestroy() {
        super.onDestroy()
        _selectLocationViewModel.onClear()
    }
}