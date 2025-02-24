package com.pro.book.model

class Feedback {
    var name: String? = null
    var phone: String? = null
    @JvmField
    var email: String? = null
    @JvmField
    var comment: String? = null

    constructor()

    constructor(name: String?, phone: String?, email: String?, comment: String?) {
        this.name = name
        this.phone = phone
        this.email = email
        this.comment = comment
    }
}
