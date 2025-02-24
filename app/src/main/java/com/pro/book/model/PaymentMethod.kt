package com.pro.book.model

class PaymentMethod {
    @JvmField
    var id: Int = 0
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var isSelected: Boolean = false

    constructor()

    constructor(id: Int, name: String?, description: String?) {
        this.id = id
        this.name = name
        this.description = description
    }
}
