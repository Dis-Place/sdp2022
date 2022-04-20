package com.github.displace.sdp2022.authentication

import android.content.Context.MODE_PRIVATE
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.displace.sdp2022.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TempLoginActivityRememberMeTest {
    private var checkBoxState: Boolean = false
    private val context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setup() {
        val sharedPreferences = context.getSharedPreferences("login-checkbox", MODE_PRIVATE)
        checkBoxState = sharedPreferences.getBoolean("login-remember", false)
        sharedPreferences.edit().putBoolean("login-remember", true).apply()

    }

    @After
    fun unsetup() {
        val sharedPreferences = context.getSharedPreferences("login-checkbox", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("login-remember", checkBoxState).apply()
    }

    @get:Rule
    val testRule = ActivityScenarioRule(TempLoginActivity::class.java)

    @Test
    fun signInButtonIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGoogleSignIn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkboxIsDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.loginRememberCheckBox))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
