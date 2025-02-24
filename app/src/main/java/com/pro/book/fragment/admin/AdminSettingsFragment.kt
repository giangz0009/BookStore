package com.pro.book.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.pro.book.R
import com.pro.book.activity.LoginActivity
import com.pro.book.activity.admin.AdminFeedbackActivity
import com.pro.book.activity.admin.AdminRevenueActivity
import com.pro.book.activity.admin.AdminRoleActivity
import com.pro.book.activity.admin.AdminTopProductActivity
import com.pro.book.activity.admin.AdminVoucherActivity
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity

class AdminSettingsFragment : Fragment() {
    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_admin_settings, container, false)

        setupScreen()

        return mView
    }

    private fun setupScreen() {
        val tvEmail = mView!!.findViewById<TextView>(R.id.tv_email)
        tvEmail.text = DataStoreManager.user?.email
        val tvManageRole = mView!!.findViewById<TextView>(R.id.tv_manage_role)
        if (Constant.MAIN_ADMIN == DataStoreManager.user?.email) {
            tvManageRole.visibility = View.VISIBLE
        } else {
            tvManageRole.visibility = View.GONE
        }

        tvManageRole.setOnClickListener { onClickManageRole() }
        mView!!.findViewById<View>(R.id.tv_manage_revenue)
            .setOnClickListener { onClickManageRevenue() }
        mView!!.findViewById<View>(R.id.tv_manage_top_product)
            .setOnClickListener { onClickManageTopProduct() }
        mView!!.findViewById<View>(R.id.tv_manage_voucher)
            .setOnClickListener { onClickManageVoucher() }
        mView!!.findViewById<View>(R.id.tv_manage_feedback)
            .setOnClickListener { onClickManageFeedback() }
        mView!!.findViewById<View>(R.id.tv_sign_out)
            .setOnClickListener { onClickSignOut() }
    }

    private fun onClickManageRole() {
        startActivity(requireActivity(), AdminRoleActivity::class.java)
    }

    private fun onClickManageRevenue() {
        startActivity(requireActivity(), AdminRevenueActivity::class.java)
    }

    private fun onClickManageTopProduct() {
        startActivity(requireActivity(), AdminTopProductActivity::class.java)
    }

    private fun onClickManageVoucher() {
        startActivity(requireActivity(), AdminVoucherActivity::class.java)
    }

    private fun onClickManageFeedback() {
        startActivity(requireActivity(), AdminFeedbackActivity::class.java)
    }

    private fun onClickSignOut() {
        if (activity == null) return
        FirebaseAuth.getInstance().signOut()
        DataStoreManager.user = null
        startActivity(requireActivity(), LoginActivity::class.java)
        requireActivity().finishAffinity()
    }
}
