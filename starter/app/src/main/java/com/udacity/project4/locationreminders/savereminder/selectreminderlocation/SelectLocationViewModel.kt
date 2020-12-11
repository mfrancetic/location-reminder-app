package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

class SelectLocationViewModel(val app: Application) : BaseViewModel(app) {

    private val _onSaveLocationClicked = MutableLiveData<Boolean>()
    val onSaveLocationClicked: LiveData<Boolean>
        get() = _onSaveLocationClicked

    private val _selectedLocation = MutableLiveData<ReminderDTO>()
    val selectedLocation: LiveData<ReminderDTO>
        get() = _selectedLocation

    init {
        _selectedLocation.value = null
        _onSaveLocationClicked.value = false
    }

    fun setLocation(location: String, latitude: Double, longitude: Double) {
        _selectedLocation.value = ReminderDTO(null, null, location, latitude, longitude)
    }

    fun onSaveLocationClicked() {
        _onSaveLocationClicked.value = true
    }

    fun onSaveLocationDone() {
        _onSaveLocationClicked.value = false
    }

    fun onClear() {
        _selectedLocation.value = null
        _onSaveLocationClicked.value = false
    }
}