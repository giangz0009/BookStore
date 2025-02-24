package com.pro.book.prefs

import android.content.Context
import com.google.gson.Gson
import com.pro.book.model.User
import com.pro.book.utils.StringUtil.isEmpty

class DataStoreManager {
    private var sharedPreferences: MySharedPreferences? = null

    companion object {
        private const val PREF_USER_INFO: String = "PREF_USER_INFO"

        private var instance: DataStoreManager? = null
        fun init(context: Context) {
            instance = DataStoreManager()
            instance!!.sharedPreferences = MySharedPreferences(context)
        }

        fun getInstance(): DataStoreManager? {
            if (instance != null) {
                return instance
            } else {
                throw IllegalStateException("Not initialized")
            }
        }

        var user: User?
            get() {
                val jsonUser = getInstance()?.sharedPreferences!!.getStringValue(PREF_USER_INFO)
                if (!isEmpty(jsonUser)) {
                    return Gson().fromJson(jsonUser, User::class.java)
                }
                return User()
            }
            set(user) {
                var jsonUser: String? = ""
                if (user != null) {
                    jsonUser = user.toJSon()
                }
                getInstance()!!.sharedPreferences?.putStringValue(PREF_USER_INFO, jsonUser)
            }
    }
}
