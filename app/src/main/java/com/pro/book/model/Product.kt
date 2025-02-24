package com.pro.book.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Entity(tableName = "product")
class Product : Serializable {
    @JvmField
    @PrimaryKey
    var id: Long = 0
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var price: Int = 0
    @JvmField
    var image: String? = null
    @JvmField
    var banner: String? = null
    @JvmField
    var category_id: Long = 0
    @JvmField
    var category_name: String? = null
    @JvmField
    var sale: Int = 0
    var isFeatured: Boolean = false
    @JvmField
    var info: String? = null

    @Ignore
    var rating: HashMap<String, Rating>? = null

    @JvmField
    var count: Int = 0
    @JvmField
    var totalPrice: Int = 0
    @JvmField
    var priceOneProduct: Int = 0

    val realPrice: Int
        get() {
            if (sale <= 0) {
                return price
            }
            return price - (price * sale / 100)
        }

    val countReviews: Int
        get() {
            if (rating == null || rating!!.isEmpty()) return 0
            return rating!!.size
        }

    val rate: Double
        get() {
            if (rating == null || rating!!.isEmpty()) return 0.0
            var sum = 0.0
            for (ratingEntity in rating!!.values) {
                sum += ratingEntity.rate
            }
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = '.'
            val formatter = DecimalFormat("#.#")
            formatter.decimalFormatSymbols = symbols
            return formatter.format(sum / rating!!.size).toDouble()
        }
}
