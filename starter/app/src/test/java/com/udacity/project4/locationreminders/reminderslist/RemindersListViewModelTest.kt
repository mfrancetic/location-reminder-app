package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainTestCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get: Rule
    var mainCoroutineRule = MainTestCoroutineRule()

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var dataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        val reminder = ReminderDTO(
                "title", "description", "location", 14.882,
                15.822
        )
        val reminder2 = ReminderDTO(
                "title2", "description2", "location2", 14.882,
                15.822
        )
        val reminders = mutableListOf(reminder, reminder2)
        dataSource = FakeDataSource(reminders)

        remindersListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext() as Application,
                dataSource
        )
    }

    @After
    fun cleanupDataSource() = runBlocking {
        dataSource.deleteAllReminders()
        stopKoin()
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testLoadReminders_2Reminders() {
        remindersListViewModel.loadReminders()

        val remindersList = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(remindersList?.size, IsEqual(2))
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testLoadReminders_noReminders() = runBlockingTest {
        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()

        val remindersList = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(remindersList?.size, IsEqual(0))
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testInvalidateShowNoData() = runBlocking {
        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()

        val showNoData = remindersListViewModel.showNoData.getOrAwaitValue()

        assertThat(showNoData.toString(), CoreMatchers.`is`("true"))
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        val showLoadingBefore = remindersListViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoadingBefore.toString(), CoreMatchers.`is`("true"))

        mainCoroutineRule.resumeDispatcher()

        val showLoadingAfter = remindersListViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoadingAfter.toString(), CoreMatchers.`is`("false"))
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {
        dataSource.setReturnError(true)

        remindersListViewModel.loadReminders()

        val snackbarText = remindersListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(snackbarText, IsEqual("Test exception"))
    }
}