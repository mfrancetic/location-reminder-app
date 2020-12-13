package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainAndroidTestCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get: Rule
    val mainCoroutineRule = MainAndroidTestCoroutineRule()

    @Before
    fun initDatabaseAndRepository() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java)
                .build()
        remindersRepository = RemindersLocalRepository(
                database.reminderDao(), Dispatchers.Main
        )
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveReminders_get2Reminders() = runBlocking {
        val reminder1 = ReminderDTO("title1", "description1", "location1",
                11.111, 11.112)
        val reminder2 = ReminderDTO("title2", "description2", "location2",
                12.111, 12.112)

        remindersRepository.saveReminder(reminder1)
        remindersRepository.saveReminder(reminder2)

        val reminders = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>

        assertThat(reminders, notNullValue())
        assertThat(reminders.data.size, `is`(2))
        assertThat(reminders.data, hasItem(reminder1))
        assertThat(reminders.data, hasItem(reminder2))
    }

    @Test
    fun getReminders_returns0Reminders() = runBlocking {
        val reminders = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>

        assertThat(reminders, notNullValue())
        assertThat(reminders.data.size, `is`(0))
    }

    @Test
    fun saveReminder_getReminderById() = runBlocking {
        val reminder = ReminderDTO("title1", "description1", "location1",
                11.111, 11.112)

        remindersRepository.saveReminder(reminder)

        val savedReminder = remindersRepository.getReminder(reminder.id) as Result.Success<ReminderDTO>

        MatcherAssert.assertThat(savedReminder.data, notNullValue())
        MatcherAssert.assertThat(savedReminder.data.id, `is`(reminder.id))
        MatcherAssert.assertThat(savedReminder.data.title, `is`(reminder.title))
        MatcherAssert.assertThat(savedReminder.data.description, `is`(reminder.description))
        MatcherAssert.assertThat(savedReminder.data.location, `is`(reminder.location))
        MatcherAssert.assertThat(savedReminder.data.latitude, `is`(reminder.latitude))
        MatcherAssert.assertThat(savedReminder.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun noReminders_getReminderById() = runBlocking {
        val reminder = remindersRepository.getReminder("0") as Result.Error

        MatcherAssert.assertThat(reminder.message, notNullValue())
        MatcherAssert.assertThat(reminder.message, `is`("Reminder not found!"))
    }

    @Test
    fun deleteAllRemindersAndGetReminders() = runBlocking {
        val reminder = ReminderDTO("title", "description", "location", 14.882,
                15.822)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 14.882,
                15.822)
        remindersRepository.saveReminder(reminder)
        remindersRepository.saveReminder(reminder2)

        val savedReminders = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>
        MatcherAssert.assertThat(savedReminders.data, notNullValue())
        MatcherAssert.assertThat(savedReminders.data.size, `is`(2))
        MatcherAssert.assertThat(savedReminders.data, hasItem(reminder))
        MatcherAssert.assertThat(savedReminders.data, hasItem(reminder2))

        remindersRepository.deleteAllReminders()
        val reminders = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>
        MatcherAssert.assertThat(reminders.data, notNullValue())
        MatcherAssert.assertThat(reminders.data.size, `is`(0))
    }

    @Test
    fun getRemindersWhenNoDataFound() = runBlocking {
        val savedReminders = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>
        MatcherAssert.assertThat(savedReminders, notNullValue())
        MatcherAssert.assertThat(savedReminders.data.size, `is`(0))
    }
}