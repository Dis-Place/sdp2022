package com.github.displace.sdp2022.matchMaking

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.users.CompleteUser

abstract class MMView : AppCompatActivity() {


    abstract fun setFriendList(activeUser : CompleteUser)
    abstract fun uiToSetup()
    abstract fun checkNonEmpty() : String
    abstract fun updateUI()
    abstract fun uiToSearch()
    abstract fun <T: View> changeVisibility(id: Int, visibility: Int)
    abstract fun isGroupVisible(id: Int): Boolean

}
