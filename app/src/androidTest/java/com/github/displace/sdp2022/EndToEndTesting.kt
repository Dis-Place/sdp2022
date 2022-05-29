/*import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.profile.ProfileActivity
import com.github.displace.sdp2022.profile.friends.FriendViewHolder
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.OfflineUserFetcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class EndToEndTesting {

    @Test
    fun testMainMenuEndToEnd() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

        val scenario = ActivityScenario.launch<ProfileActivity>(intent)

        scenario.use {
            onView(ViewMatchers.withId(R.id.mainActivityLogInButton)).perform(click())  //start
            Thread.sleep(1000)
            onView(ViewMatchers.withId(R.id.signInActivityGuestModeButton)).perform(click())  //login
            Thread.sleep(1000)
            //main menu
            onView(ViewMatchers.withId(R.id.newsButton)).perform(click())
            Thread.sleep(1000)
            //news
            pressBack()
            Thread.sleep(1000)
            //main menu
            onView(ViewMatchers.withId(R.id.settingsButton)).perform(click())
            Thread.sleep(1000)
            //settings
            pressBack()
            Thread.sleep(1000)
            //main menu
            onView(ViewMatchers.withId(R.id.profileButton)).perform(click())
            Thread.sleep(1000)
            //profile
            pressBack()
            Thread.sleep(1000)
            //main menu
            onView(ViewMatchers.withId(R.id.playButton)).perform(RecyclerViewActions.actionOnItemAtPosition<FriendViewHolder>(0, click()))
            Thread.sleep(1000)
            //gameList
            pressBack()
            Thread.sleep(1000)
            //main menu
        }
    }
}*/