package com.pro.book.model

import com.google.gson.Gson

class User {
    var email: String? = null
    @JvmField
    var password: String? = null
    @JvmField
    var isAdmin: Boolean = false

    constructor()

    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }

    fun toJSon(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}
