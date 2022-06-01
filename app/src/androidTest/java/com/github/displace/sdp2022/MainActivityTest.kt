import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.displace.sdp2022.MainActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.authentication.SignInActivity
import com.github.displace.sdp2022.users.CompleteUser
import com.github.displace.sdp2022.users.OfflineUserFetcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
     private lateinit var copyCompleteUser: CompleteUser
   //  private val context = InstrumentationRegistry.getInstrumentation().context
     private val context : Context = ApplicationProvider.getApplicationContext()

     @get:Rule
     val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    //TODO: May have to re-do these tests because I'm not sure it really tests the functionalities

    @Test
     fun logInConnectionRemember() {
         val bool =
             context.getSharedPreferences("login", MODE_PRIVATE).getBoolean("remembered", false)
         context.getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("remembered", true)
             .apply()
         Intents.init()
         onView(withId(R.id.mainActivityLogInButton)).perform(click())
         IntentMatchers.hasComponent(SignInActivity::class.java.name)
         Intents.release()
         context.getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("remembered", bool)
             .apply()
     }

     @Test
     fun logInConnectionNotRemember() {
         val bool =
             context.getSharedPreferences("login", MODE_PRIVATE).getBoolean("remembered", false)
         context.getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("remembered", false)
             .apply()
         Intents.init()
         onView(withId(R.id.mainActivityLogInButton)).perform(click())
         IntentMatchers.hasComponent(SignInActivity::class.java.name)
         Intents.release()
         context.getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("remembered", bool)
             .apply()
     }
}