package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.authentication.AuthenticationViewModel
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationViewModel
import it.xabaras.android.espresso.recyclerviewchildactions.RecyclerViewChildActions
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
class ReminderDescriptionActivityTest :
    AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @Before
    fun init() = runBlocking {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            viewModel {
                AuthenticationViewModel(
                    appContext,
                )
            }
            viewModel {
                SelectLocationViewModel(
                    appContext,
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { RemindersLocalRepository(get()) }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
        val reminder1 = ReminderDTO(
            "title1", "description1", "location1",
            11.111, 11.112
        )
        repository.saveReminder(reminder1)
    }

    @Test
    fun testReminderDescriptionActivity_displaysReminderDataFromRemindersList() {
        launch(RemindersActivity::class.java)
        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView)).perform(
            RecyclerViewChildActions.Companion.actionOnChild(
                ViewActions.click(), R.id.reminderCardView
            )
        )

        Espresso.onView(withText("title1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(appContext.getString(R.string.description) + "description1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(appContext.getString(R.string.location) + "location1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(appContext.getString(R.string.latitude) + "11.111"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(appContext.getString(R.string.longitude) + "11.112"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}