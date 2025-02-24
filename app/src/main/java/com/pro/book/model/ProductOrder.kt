package com.pro.book.model

import java.io.Serializable

class ProductOrder : Serializable {
    @JvmField
    var id: Long = 0
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var count: Int = 0
    @JvmField
    var price: Int = 0
    @JvmField
    var image: String? = null

    constructor()

    constructor(
        id: Long,
        name: String?,
        description: String?,
        count: Int,
        price: Int,
        image: String?
    ) {
        this.id = id
        this.name = name
        this.description = description
        this.count = count
        this.price = price
        this.image = image
    }
}
