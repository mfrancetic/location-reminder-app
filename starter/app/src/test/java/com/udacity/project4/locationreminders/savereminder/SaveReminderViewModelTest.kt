package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainTestCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get: Rule
    var mainCoroutineRule = MainTestCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var context: Context

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        context = ApplicationProvider.getApplicationContext()

        dataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
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
    fun testOnClear() = mainCoroutineRule.runBlockingTest {
        saveReminderViewModel.onClear()

        assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
           CoreMatchers.`is`(nullValue())
        )
        assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            CoreMatchers.`is`(nullValue())
        )
        assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            CoreMatchers.`is`(nullValue())
        )
        assertThat(
            saveReminderViewModel.selectedPOI.getOrAwaitValue(),
            CoreMatchers.`is`(nullValue())
        )
        assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            CoreMatchers.`is`(nullValue())
        )
        assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            CoreMatchers.`is`(nullValue())
        )
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testValidateEnteredData_returnsTrue() {
        val title = "title"
        val location = "location"
        val reminderDataItem = ReminderDataItem(
            title, null, location,
            null, null
        )
        val isValid = saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid.toString(), CoreMatchers.`is`("true"))
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testValidateEnteredData_LocationMissing_returnsFalse() {
        val title = "title"
        val reminderDataItem = ReminderDataItem(
            title, null, null,
            null, null
        )
        val isValid = saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid.toString(), CoreMatchers.`is`("false"))
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_select_location)
        )
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testValidateEnteredData_TitleMissing_returnsFalse() {
        val location = "location"
        val reminderDataItem = ReminderDataItem(
            null, null, location,
            null, null
        )
        val isValid = saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid.toString(), CoreMatchers.`is`("false"))
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_enter_title)
        )
    }


    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun validateAndSaveReminder_invalidDataItem_returnsNul() = mainCoroutineRule.runBlockingTest {
        val location = "location"
        val reminderDataItem = ReminderDataItem(
            null, null, location,
            null, null
        )

        assertThat(
            saveReminderViewModel.validateEnteredData(reminderDataItem).toString(),
            CoreMatchers.`is`("false")
        )
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_enter_title)
        )

        assertThat(
            saveReminderViewModel.validateAndSaveReminder(reminderDataItem).toString(),
            CoreMatchers.`is`("null"),
        )
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = ReminderDataItem(
            "title", "description", "location",
            14.111, 14.112
        )
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

        val showLoadingBefore = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoadingBefore.toString(), CoreMatchers.`is`("true"))

        mainCoroutineRule.resumeDispatcher()

        val showLoadingAfter = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoadingAfter.toString(), CoreMatchers.`is`("false"))

        val showToastAfter = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(showToastAfter, CoreMatchers.`is`(context.getString(R.string.reminder_saved)))

        val showNavigationCommand = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertThat(showNavigationCommand, CoreMatchers.`is`(NavigationCommand.Back))
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {
        dataSource.setReturnError(true)

        val reminderDataItem = ReminderDataItem(
            null, "description", "location",
            14.111, 14.112
        )
        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

        val result = dataSource.getReminder(reminderDataItem.id)
        assertThat(result, IsEqual(Result.Error("Test exception")))
    }
}