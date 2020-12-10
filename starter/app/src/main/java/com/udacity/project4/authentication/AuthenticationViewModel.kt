package com.udacity.project4.authentication

import android.app.Application
import android.content.Context
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.base.BaseViewModel

class AuthenticationViewModel(val app: Application) :
    BaseViewModel(app) {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun logout(context: Context) {
        AuthUI.getInstance().signOut(context)
    }
}