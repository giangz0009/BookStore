package com.pro.book.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.pro.book.R
import com.pro.book.model.User
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.StringUtil.isEmpty

class ChangePasswordActivity : BaseActivity() {
    private var edtOldPassword: EditText? = null
    private var edtNewPassword: EditText? = null
    private var edtConfirmPassword: EditText? = null
    private var btnChangePassword: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initToolbar()
        initUi()
        initListener()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.change_password)
    }

    private fun initUi() {
        edtOldPassword = findViewById(R.id.edt_old_password)
        edtNewPassword = findViewById(R.id.edt_new_password)
        edtConfirmPassword = findViewById(R.id.edt_confirm_password)
        btnChangePassword = findViewById(R.id.btn_change_password)
    }

    private fun initListener() {
        edtOldPassword!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    edtOldPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    edtOldPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }
            }
        })
        edtNewPassword!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    edtNewPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    edtNewPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }
            }
        })
        edtConfirmPassword!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    edtConfirmPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    edtConfirmPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }
            }
        })

        btnChangePassword!!.setOnClickListener { onClickValidateChangePassword() }
    }

    private fun onClickValidateChangePassword() {
        val strOldPassword = edtOldPassword!!.text.toString().trim { it <= ' ' }
        val strNewPassword = edtNewPassword!!.text.toString().trim { it <= ' ' }
        val strConfirmPassword = edtConfirmPassword!!.text.toString().trim { it <= ' ' }
        if (isEmpty(strOldPassword)) {
            showToastMessage(getString(R.string.msg_old_password_require))
        } else if (isEmpty(strNewPassword)) {
            showToastMessage(getString(R.string.msg_new_password_require))
        } else if (isEmpty(strConfirmPassword)) {
            showToastMessage(getString(R.string.msg_confirm_password_require))
        } else if (!DataStoreManager.user?.password.equals(strOldPassword)) {
            showToastMessage(getString(R.string.msg_old_password_invalid))
        } else if (strNewPassword != strConfirmPassword) {
            showToastMessage(getString(R.string.msg_confirm_password_invalid))
        } else if (strOldPassword == strNewPassword) {
            showToastMessage(getString(R.string.msg_new_password_invalid))
        } else {
            changePassword(strNewPassword)
        }
    }

    private fun changePassword(newPassword: String) {
        showProgressDialog(true)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        user.updatePassword(newPassword)
            .addOnCompleteListener { task: Task<Void?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    showToastMessage(getString(R.string.msg_change_password_successfully))
                    val userLogin: User = DataStoreManager.user!!
                    userLogin.password = newPassword
                    DataStoreManager.user = userLogin
                    edtOldPassword!!.setText("")
                    edtNewPassword!!.setText("")
                    edtConfirmPassword!!.setText("")
                }
            }
    }
}