package com.github.displace.sdp2022.profile.qrcode

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class QrCodeScannerActivity : AppCompatActivity() {

    private lateinit var codeScanner : CodeScanner
    private lateinit var scannerView : CodeScannerView

    /**
     * Sets up the scanner, will be modularized after testing
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        scannerView = findViewById(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        /**
         * setups parameters
         */
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        /**
         * setup callbacks
         */
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val scanned = it.text
                try{
                    val user = Json.decodeFromString<PartialUser>(scanned)
                    showScanPrompt(user)
                }catch ( e  : Exception){
                    Toast.makeText(this, "SCAN IS NOT CORRECT", Toast.LENGTH_LONG).show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }



    /**
     * Show the prompt to confirm the scan of someone else's code
     * Will need to :
     *  - let the user confirm that the scan is correct
     *  - create the friend invitation
     *  - return to the previous activity (which is QrCodeTemp for now : use a simple intent)
     */
    fun showScanPrompt(partialUser : PartialUser){
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setMessage("Send invite to ${partialUser.username} ?")
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        alertDialogBuilder.setPositiveButton("Ok") { dialog,_ ->
            //send invite
            //quit
        }
        alertDialogBuilder.create()

    }

    /**
     * startPreview reactivates the camera
     */
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    /**
     * releaseResources makes sure the camera does not stay activated
     */
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}