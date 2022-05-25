package com.github.displace.sdp2022.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.github.displace.sdp2022.R

/**
 * Util to create and show Progress Dialogs
 */
object ProgressDialogsUtil {
    var progressDialog: AlertDialog? = null

    /**
     * Creates and show a new progress dialog
     * @param context: Context of the activity needing the progress dialog
     */
    fun showProgressDialog(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(view)
        progressDialog = dialogBuilder.create()
        progressDialog?.show()
    }

    /**
     * Removes the progress dialog from the view
     */
    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null       // We do not need it anymore and we need to have it at null in case the function is called twice in a row
    }
}