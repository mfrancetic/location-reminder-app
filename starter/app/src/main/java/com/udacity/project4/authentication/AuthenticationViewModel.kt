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

    private val _navigateBackToAuthenticationActivity = MutableLiveData<Boolean>()
    val navigateBackToAuthenticationActivity: LiveData<Boolean>
        get() = _navigateBackToAuthenticationActivity

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    init {
        _launchSignInFlow.value = false
        _navigateBackToAuthenticationActivity.value = false
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun logout(context: Context) {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener {
                    deleteUserAccount(context)
                }
    }

    private fun deleteUserAccount(context: Context) {
        AuthUI.getInstance()
                .delete(context)
                .addOnCompleteListener {
                    _navigateBackToAuthenticationActivity.value = true
                }
    }

    fun onLoginClicked() {
        _launchSignInFlow.value = true
    }

    fun onLoginFlowDone() {
        _launchSignInFlow.value = false
    }

    fun navigateBackToAuthenticationActivityDone() {
        _navigateBackToAuthenticationActivity.value = false
    }
}