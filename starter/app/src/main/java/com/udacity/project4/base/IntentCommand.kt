package com.udacity.project4.base

import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

/**
 * Sealed class used with the live data to navigate between the activities
 */
sealed class IntentCommand {
    /**
     * navigate to an activity
     */
    data class ToReminderActivity(val from: AppCompatActivity, val to: Class<RemindersActivity>) :
        IntentCommand()

    /**
     * navigate to an activity
     */
    data class ToReminderDescriptionActivity(
        val from: AppCompatActivity, val to: Class<ReminderDescriptionActivity>,
        val item: ReminderDataItem
    ) : IntentCommand()

    /**
     * navigate back to the previous activity
     */
    object Back : IntentCommand()

    /**
     * navigate back to a destination in the back stack
     */
    data class BackTo(val activity: Class<AuthenticationActivity>) : IntentCommand()
}