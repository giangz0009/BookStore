package com.pro.book.activity

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.pro.book.R

abstract class BaseActivity : AppCompatActivity() {
    private var progressDialog: MaterialDialog? = null
    private var alertDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createProgressDialog()
        createAlertDialog()
    }

    private fun createProgressDialog() {
        progressDialog = MaterialDialog.Builder(this)
            .content(R.string.msg_waiting_message)
            .progress(true, 0)
            .build()
    }

    fun showProgressDialog(value: Boolean) {
        if (value) {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
                progressDialog!!.setCancelable(false)
            }
        } else {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }

        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
    }

    private fun createAlertDialog() {
        alertDialog = MaterialDialog.Builder(this)
            .title(R.string.app_name)
            .positiveText(R.string.action_ok)
            .cancelable(false)
            .build()
    }

    fun showAlertDialog(errorMessage: String?) {
        alertDialog!!.setContent(errorMessage)
        alertDialog!!.show()
    }

    fun showAlertDialog(@StringRes resourceId: Int) {
        alertDialog!!.setContent(resourceId)
        alertDialog!!.show()
    }

    fun setCancelProgress(isCancel: Boolean) {
        if (progressDialog != null) {
            progressDialog!!.setCancelable(isCancel)
        }
    }

    fun showToastMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }

        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        super.onDestroy()
    }
}
