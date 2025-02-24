package com.pro.book.activity.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.model.Admin
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.StringUtil.isEmpty
import com.pro.book.utils.StringUtil.isValidEmail

class AdminAddRoleActivity : BaseActivity() {
    private var edtEmail: EditText? = null
    private var edtPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_role)

        initUi()
    }

    private fun initUi() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.label_add_role)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        val btnAdd = findViewById<Button>(R.id.btn_add)

        imgToolbarBack.setOnClickListener { onBackPressed() }
        btnAdd.setOnClickListener { addRole() }
    }

    private fun addRole() {
        val strEmail = edtEmail!!.text.toString().trim { it <= ' ' }
        val strPassword = edtPassword!!.text.toString().trim { it <= ' ' }
        if (isEmpty(strEmail)) {
            showToastMessage(getString(R.string.msg_email_admin_empty))
            return
        }

        if (isEmpty(strPassword)) {
            showToastMessage(getString(R.string.msg_password_admin_empty))
            return
        }

        if (!isValidEmail(strEmail)) {
            showToastMessage(getString(R.string.msg_email_invalid))
            return
        }

        if (!strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
            showToastMessage(getString(R.string.msg_email_invalid_admin))
            return
        }

        // Add admin
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val adminId = System.currentTimeMillis()
                        val admin = Admin(adminId, user.email)
                        get(this).adminDatabaseReference
                            .child(adminId.toString())
                            .setValue(admin) { _: DatabaseError?, _: DatabaseReference? ->
                                edtEmail!!.setText("")
                                edtPassword!!.setText("")
                                hideSoftKeyboard(this)
                                showToastMessage(getString(R.string.msg_add_admin_success))
                            }
                    }
                } else {
                    showToastMessage(getString(R.string.msg_register_error))
                }
            }
    }
}