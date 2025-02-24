package com.pro.book.model

import java.io.Serializable

class RatingReview(@JvmField var type: Int, @JvmField var id: String) : Serializable {
    companion object {
        const val TYPE_RATING_REVIEW_PRODUCT: Int = 1
        const val TYPE_RATING_REVIEW_ORDER: Int = 2
    }
}
