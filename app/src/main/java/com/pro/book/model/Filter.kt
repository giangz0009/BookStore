package com.pro.book.model

class Filter {
    @JvmField
    var id: Int = 0
    @JvmField
    var name: String? = null
    @JvmField
    var isSelected: Boolean = false

    constructor()

    constructor(id: Int, name: String?) {
        this.id = id
        this.name = name
    }

    companion object {
        const val TYPE_FILTER_ALL: Int = 1
        const val TYPE_FILTER_RATE: Int = 2
        const val TYPE_FILTER_PRICE: Int = 3
        const val TYPE_FILTER_PROMOTION: Int = 4
    }
}
