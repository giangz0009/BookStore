package com.pro.book.model

import java.io.Serializable

class Rating : Serializable {
    @JvmField
    var review: String? = null
    @JvmField
    var rate: Double = 0.0

    constructor()

    constructor(review: String?, rate: Double) {
        this.review = review
        this.rate = rate
    }
}
