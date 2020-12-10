package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.base.IntentCommand
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import org.koin.android.ext.android.inject
import org.koin.core.logger.KOIN_TAG

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    val _viewModel: AuthenticationViewModel by inject()
    private lateinit var binding: ActivityAuthenticationBinding

    companion object {
        private const val SIGN_IN_RESULT_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_authentication
        )

        binding.viewModel = _viewModel

        setupObservers()
    }

    private fun setupObservers() {
        _viewModel.launchSignInFlow.observe(this, { launchSignInFlow ->
            if (launchSignInFlow) {
                launchSignInFlow()
                _viewModel.onLoginFlowDone()
            }
        })

        _viewModel.authenticationState.observe(this, { authenticationState ->
            if (authenticationState == AuthenticationViewModel.AuthenticationState.AUTHENTICATED) {
                navigateToRemindersActivity()
            }
        })

        _viewModel.intentCommand.observe(this, { command ->
            if (command is IntentCommand.To) {
                val intent = Intent(command.from, command.to)
                startActivity(intent)
            }
        })
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.FirebaseTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .setAvailableProviders(providers)
                        .build(),
                SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                navigateToRemindersActivity()
            } else {
                Log.i(KOIN_TAG, response?.error?.errorCode.toString())
            }
        }
    }

    private fun navigateToRemindersActivity() {
        _viewModel.intentCommand.postValue(
                IntentCommand.To(this, RemindersActivity::class.java)
        )
    }
}