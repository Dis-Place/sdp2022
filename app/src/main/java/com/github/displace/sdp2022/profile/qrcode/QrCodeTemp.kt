package com.github.displace.sdp2022.profile.qrcode

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


//the QR code does not need to receive any information for its creation
class QrCodeTemp : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_temp)
    }


    /**
     * Generate a bitmap to be used by a QR code and put its information in the corresponding field
     * If we want to use this function somewhere else : place it in some Utils
     * @argument partialUser : the partial user for which to create the bitmap
     */
    private fun generateQrCodeBitmap(partialUser : PartialUser) : Bitmap? {
        val width = 800
        val height = 800
        val qrCodeContent : String = Json.encodeToString(partialUser)

        try{
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(qrCodeContent, BarcodeFormat.QR_CODE, width, height)
            return bitmap
        } catch(e : Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Create the dialog view which will show the QR code
     */
    private fun createImagePopup(bmp : Bitmap){

        val imageDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout: View = inflater.inflate(
            R.layout.custom_image_dialog,
            null
        )
        val image = layout.findViewById<View>(R.id.fullimage) as ImageView

        image.setImageBitmap(bmp)

        imageDialog.setView(layout)
        imageDialog.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })

        imageDialog.create()
        imageDialog.show()
    }

    /**
     * Show the QR code corresponding to the partial user after generating the bitmap
     */
    fun showQrCode(view : View){
        val app = applicationContext as MyApplication
        val bmp = generateQrCodeBitmap(app.getActiveUser()!!.getPartialUser())
        if(bmp != null){
            createImagePopup(bmp)
        }

    }


    /**
     * Transition to the scanning activity
     */
    fun useScanner(view : View){
        val intent = Intent(this, QrCodeScannerActivity::class.java)
        startActivity(intent)
    }



}