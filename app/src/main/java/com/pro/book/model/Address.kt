package com.pro.book.model

import java.io.Serializable

class Address : Serializable {
    @JvmField
    var id: Long = 0
    @JvmField
    var name: String? = null
    @JvmField
    var phone: String? = null
    @JvmField
    var address: String? = null

    var userEmail: String? = null
    @JvmField
    var isSelected: Boolean = false

    constructor()

    constructor(id: Long, name: String?, phone: String?, address: String?, userEmail: String?) {
        this.id = id
        this.name = name
        this.phone = phone
        this.address = address
        this.userEmail = userEmail
    }
}