package com.pro.book.activity.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.BaseActivity
import com.pro.book.adapter.admin.AdminFeedbackAdapter
import com.pro.book.model.Feedback

class AdminFeedbackActivity : BaseActivity() {
    private var listFeedback: MutableList<Feedback>? = null
    private var adminFeedbackAdapter: AdminFeedbackAdapter? = null
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_feedback)

        initToolbar()
        initUi()
        loadListFeedbackFromFirebase()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { onBackPressed() }
        tvToolbarTitle.text = getString(R.string.feedback)
    }

    private fun initUi() {
        val rcvData = findViewById<RecyclerView>(R.id.rcv_data)
        val linearLayoutManager = LinearLayoutManager(this)
        rcvData.layoutManager = linearLayoutManager
        listFeedback = ArrayList()
        adminFeedbackAdapter = AdminFeedbackAdapter(listFeedback)
        rcvData.adapter = adminFeedbackAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListFeedbackFromFirebase() {
        showProgressDialog(true)
        mValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showProgressDialog(false)
                resetListFeedback()
                for (dataSnapshot in snapshot.children) {
                    val feedback = dataSnapshot.getValue(Feedback::class.java) ?: return
                    listFeedback!!.add(0, feedback)
                }
                if (adminFeedbackAdapter != null) adminFeedbackAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showProgressDialog(false)
                showToastMessage(getString(R.string.msg_get_date_error))
            }
        }
        get(this).feedbackDatabaseReference
            .addValueEventListener(mValueEventListener!!)
    }

    private fun resetListFeedback() {
        if (listFeedback != null) {
            listFeedback!!.clear()
        } else {
            listFeedback = ArrayList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mValueEventListener != null) {
            get(this).feedbackDatabaseReference
                .removeEventListener(mValueEventListener!!)
        }
    }
}
