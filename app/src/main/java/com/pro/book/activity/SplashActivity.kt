package com.pro.book.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.pro.book.R
import com.pro.book.activity.admin.AdminMainActivity
import com.pro.book.prefs.DataStoreManager
import com.pro.book.utils.GlobalFunction.startActivity
import com.pro.book.utils.StringUtil.isEmpty

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ this.goToActivity() }, 2000)
    }

    private fun goToActivity() {
        if (DataStoreManager.user != null
            && !isEmpty(DataStoreManager.user!!.email)
        ) {
            if (DataStoreManager.user!!.isAdmin) {
                startActivity(this, AdminMainActivity::class.java)
            } else {
                startActivity(this, MainActivity::class.java)
            }
        } else {
            startActivity(this, LoginActivity::class.java)
        }
        finish()
    }
}
