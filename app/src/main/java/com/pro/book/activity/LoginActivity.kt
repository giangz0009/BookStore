package com.pro.book.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.pro.book.R
import com.pro.book.activity.admin.AdminMainActivity
import com.pro.book.model.User
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity
import com.pro.book.utils.StringUtil.isEmpty
import com.pro.book.utils.StringUtil.isValidEmail

class LoginActivity : BaseActivity() {
    private var edtEmail: EditText? = null
    private var edtPassword: EditText? = null
    private var btnLogin: Button? = null
    private var layoutRegister: LinearLayout? = null
    private var tvForgotPassword: TextView? = null
    private var isEnableButtonLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        initUi()
        initListener()
    }

    private fun initUi() {
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btn_login)
        layoutRegister = findViewById(R.id.layout_register)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
    }

    private fun initListener() {
        edtEmail!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    edtEmail!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    edtEmail!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }

                val strPassword = edtPassword!!.text.toString().trim { it <= ' ' }
                if (!isEmpty(s.toString()) && !isEmpty(strPassword)) {
                    isEnableButtonLogin = true
                    btnLogin!!.setBackgroundResource(R.drawable.bg_button_enable_corner_16)
                } else {
                    isEnableButtonLogin = false
                    btnLogin!!.setBackgroundResource(R.drawable.bg_button_disable_corner_16)
                }
            }
        })

        edtPassword!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    edtPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    edtPassword!!.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }

                val strEmail = edtEmail!!.text.toString().trim { it <= ' ' }
                if (!isEmpty(s.toString()) && !isEmpty(strEmail)) {
                    isEnableButtonLogin = true
                    btnLogin!!.setBackgroundResource(R.drawable.bg_button_enable_corner_16)
                } else {
                    isEnableButtonLogin = false
                    btnLogin!!.setBackgroundResource(R.drawable.bg_button_disable_corner_16)
                }
            }
        })

        layoutRegister!!.setOnClickListener {
            startActivity(
                this, RegisterActivity::class.java
            )
        }

        btnLogin!!.setOnClickListener { onClickValidateLogin() }
        tvForgotPassword!!.setOnClickListener {
            startActivity(
                this, ForgotPasswordActivity::class.java
            )
        }
    }

    private fun onClickValidateLogin() {
        if (!isEnableButtonLogin) return

        val strEmail = edtEmail!!.text.toString().trim { it <= ' ' }
        val strPassword = edtPassword!!.text.toString().trim { it <= ' ' }
        if (isEmpty(strEmail)) {
            showToastMessage(getString(R.string.msg_email_require))
        } else if (isEmpty(strPassword)) {
            showToastMessage(getString(R.string.msg_password_require))
        } else if (!isValidEmail(strEmail)) {
            showToastMessage(getString(R.string.msg_email_invalid))
        } else {
            loginUserFirebase(strEmail, strPassword)
        }
    }

    private fun loginUserFirebase(email: String, password: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userObject = User(user.email, password)
                        if (user.email != null && user.email!!.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                            userObject.isAdmin = true
                        }
                        DataStoreManager.user = userObject
                        goToMainActivity()
                    }
                } else {
                    showToastMessage(getString(R.string.msg_login_error))
                }
            }
    }

    private fun goToMainActivity() {
        if (DataStoreManager.user!!.isAdmin) {
            startActivity(this, AdminMainActivity::class.java)
        } else {
            startActivity(this, MainActivity::class.java)
        }
        finishAffinity()
    }
}