package com.pro.book.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.model.Feedback
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.GlobalFunction.showToastMessage
import com.pro.book.utils.StringUtil.isEmpty

class FeedbackActivity : BaseActivity() {
    private var edtName: EditText? = null
    private var edtPhone: EditText? = null
    private var edtEmail: EditText? = null
    private var edtComment: EditText? = null
    private var tvSendFeedback: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        initToolbar()
        initUi()
        initData()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.feedback)
    }

    private fun initUi() {
        edtName = findViewById(R.id.edt_name)
        edtPhone = findViewById(R.id.edt_phone)
        edtEmail = findViewById(R.id.edt_email)
        edtComment = findViewById(R.id.edt_comment)
        tvSendFeedback = findViewById(R.id.tv_send_feedback)
    }

    private fun initData() {
        edtEmail?.setText(DataStoreManager.user?.email)
        tvSendFeedback!!.setOnClickListener { onClickSendFeedback() }
    }

    private fun onClickSendFeedback() {
        val strName = edtName!!.text.toString()
        val strPhone = edtPhone!!.text.toString()
        val strEmail = edtEmail!!.text.toString()
        val strComment = edtComment!!.text.toString()

        if (isEmpty(strName)) {
            showToastMessage(this, getString(R.string.name_require))
        } else if (isEmpty(strComment)) {
            showToastMessage(this, getString(R.string.comment_require))
        } else {
            showProgressDialog(true)
            val feedback = Feedback(strName, strPhone, strEmail, strComment)
            get(this).feedbackDatabaseReference
                .child(System.currentTimeMillis().toString())
                .setValue(feedback) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    sendFeedbackSuccess()
                }
        }
    }

    private fun sendFeedbackSuccess() {
        hideSoftKeyboard(this)
        showToastMessage(this, getString(R.string.msg_send_feedback_success))
        edtName!!.setText("")
        edtPhone!!.setText("")
        edtComment!!.setText("")
    }
}
