package com.udacity.project4.authentication

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.base.BaseViewModel

class AuthenticationViewModel(val app: Application) :
    BaseViewModel(app) {

    private val _launchSignInFlow = MutableLiveData<Boolean>()
    val launchSignInFlow: LiveData<Boolean>
        get() = _launchSignInFlow

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    init {
        _launchSignInFlow.value = false
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

    fun onLoginClicked() {
        _launchSignInFlow.value = true
    }

    fun onLoginFlowDone(){
        _launchSignInFlow.value = false
    }
}