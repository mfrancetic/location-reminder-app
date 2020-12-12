package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 14.882,
                15.822)
        database.reminderDao().saveReminder(reminder)

        val savedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat(savedReminder as ReminderDTO, notNullValue())
        assertThat(savedReminder.id, `is`(reminder.id))
        assertThat(savedReminder.title, `is`(reminder.title))
        assertThat(savedReminder.description, `is`(reminder.description))
        assertThat(savedReminder.location, `is`(reminder.location))
        assertThat(savedReminder.latitude, `is`(reminder.latitude))
        assertThat(savedReminder.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveRemindersAndGetReminders() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 14.882,
                15.822)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 14.882,
                15.822)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)

        val savedReminders = database.reminderDao().getReminders()

        assertThat(savedReminders, notNullValue())
        assertThat(savedReminders.size, `is`(2))
        assertThat(savedReminders, hasItem(reminder))
        assertThat(savedReminders, hasItem(reminder2))
    }

    @Test
    fun deleteAllRemindersAndGetReminders() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 14.882,
                15.822)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 14.882,
                15.822)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)

        val savedReminders = database.reminderDao().getReminders()
        assertThat(savedReminders, notNullValue())
        assertThat(savedReminders.size, `is`(2))
        assertThat(savedReminders, hasItem(reminder))
        assertThat(savedReminders, hasItem(reminder2))

        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders, notNullValue())
        assertThat(reminders.size, `is`(0))
    }

    @Test
    fun getRemindersWhenNoDataFound() = runBlockingTest {
        val savedReminders = database.reminderDao().getReminders()
        assertThat(savedReminders, notNullValue())
        assertThat(savedReminders.size, `is`(0))
    }
}