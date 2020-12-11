package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.base.BaseViewModel

class SelectLocationViewModel(val app: Application) : BaseViewModel(app) {

    private val _isMarkerSet = MutableLiveData<Boolean>()
    val isMarkerSet: LiveData<Boolean>
        get() = _isMarkerSet

    init {
        _isMarkerSet.value = false
    }

    fun setMarker() {
        _isMarkerSet.value = true
    }
}