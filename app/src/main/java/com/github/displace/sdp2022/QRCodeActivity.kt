package com.github.displace.sdp2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


// General skeleton/plan : "Add QR Code activity"

class QRCodeActivity : AppCompatActivity() {

    lateinit var trucsUtiles: ???

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val image = getGeneratedQRCode()
    }

    // "Generate QR Code" // has to use library
    fun getGeneratedQRCode() {} // Can be done in Complete User/Partial User ou autre part


    // "Scan QR Code" // has to use library
    /*
    En réaction à quand on scan le QR Code
     */
    fun scanExternalQRCode() {
        trucsUtiles = interpretScannedQRCode()
        confirmPrompt()
    }

    // "Scan QR code"
    fun interpretScannedQRCode() {}

    // "Send invite when scanning QR code"
    // Show Dialog View "Do you confirm that you want to add $username as a friend ?"
    fun confirmPrompt() {}

    // When user confirms he wants to invite
    fun inviteConfirmed() {
        sendFriendInvite(trucsUtiles, boolean acceptAutomatically = true)
    }

    // "Accept invite when receiving QR Code invite"
    // A incorporer dans les friend invites quand tu les recois
    fun personalQRCodeScanned() {
        showToastMessage("You just added $username as a friend !")
    }
}