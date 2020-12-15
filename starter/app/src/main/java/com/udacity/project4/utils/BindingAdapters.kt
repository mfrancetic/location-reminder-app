package com.udacity.project4.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.R
import com.udacity.project4.base.BaseRecyclerViewAdapter

object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    @BindingAdapter("app:description")
    @JvmStatic
    fun setDescription(view: TextView, description: String) {
        val descriptionText = view.context.getString(R.string.description) + description
        view.text = descriptionText
    }

    @BindingAdapter("app:location")
    @JvmStatic
    fun setLocation(view: TextView, location: String) {
        val locationText = view.context.getString(R.string.location) + location
        view.text = locationText
    }

    @BindingAdapter("app:latitude")
    @JvmStatic
    fun setLatitude(view: TextView, latitude: Double) {
        val latitudeText = view.context.getString(R.string.latitude) + latitude
        view.text = latitudeText
    }

    @BindingAdapter("app:longitude")
    @JvmStatic
    fun setLongitude(view: TextView, longitude: Double) {
        val longitudeText = view.context.getString(R.string.longitude) + longitude
        view.text = longitudeText
    }
}