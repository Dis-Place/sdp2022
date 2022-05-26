package com.github.displace.sdp2022.profile.qrcode

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.displace.sdp2022.R
import com.github.displace.sdp2022.users.PartialUser
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object QrCodeUtils {

    /**
     * Width and height of the QR code
     */
    private const val width = 800
    private const val height = 800

    /**
     * Generate a bitmap to be used by a QR code and put its information in the corresponding field
     * If we want to use this function somewhere else : place it in some Utils
     * @param partialUser : the partial user for which to create the bitmap
     * @return the bitmap of the partialUser or null if there was an error
     */
    fun generateQrCodeBitmap(partialUser: PartialUser): Bitmap? {

        try {
            val qrCodeContent: String = Json.encodeToString(partialUser)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.encodeBitmap(qrCodeContent, BarcodeFormat.QR_CODE, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Create and show the dialog view which will show the QR code
     * @param bmp image to show (qr code)
     * @param context (activity)
     */
    fun createImagePopup(bmp: Bitmap, context: Context) {

        //create the support for the QR code
        val imageDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        val inflater =
            context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout: View = inflater.inflate(
            R.layout.custom_image_dialog,
            null
        )
        val image = layout.findViewById<View>(R.id.fullimage) as ImageView

        image.setImageBitmap(bmp)

        imageDialog.setView(layout)
        imageDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        imageDialog.show()
    }

}