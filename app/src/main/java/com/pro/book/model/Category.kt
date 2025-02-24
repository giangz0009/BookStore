package com.pro.book.model

import java.io.Serializable

class Category : Serializable {
    @JvmField
    var id: Long = 0
    @JvmField
    var name: String? = null

    constructor()

    constructor(id: Long, name: String?) {
        this.id = id
        this.name = name
    }
}
