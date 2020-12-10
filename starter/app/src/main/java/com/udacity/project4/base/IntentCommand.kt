package com.udacity.project4.base

import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * Sealed class used with the live data to navigate between the activities
 */
sealed class IntentCommand {
    /**
     * navigate to an activity
     */
    data class To(val from: AppCompatActivity, val to: Class<RemindersActivity>) : IntentCommand()

    /**
     * navigate back to the previous activity
     */
    object Back : IntentCommand()

    /**
     * navigate back to a destination in the back stack
     */
    data class BackTo(val activity: Class<AuthenticationActivity>) : IntentCommand()
}