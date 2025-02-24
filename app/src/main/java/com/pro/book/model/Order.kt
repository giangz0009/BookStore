package com.pro.book.model

import com.pro.book.utils.StringUtil.isEmpty
import java.io.Serializable

class Order : Serializable {
    @JvmField
    var id: Long = 0
    @JvmField
    var userEmail: String? = null
    @JvmField
    var dateTime: String? = null
    @JvmField
    var products: List<ProductOrder>? = null
    @JvmField
    var price: Int = 0
    @JvmField
    var voucher: Int = 0
    @JvmField
    var total: Int = 0
    @JvmField
    var paymentMethod: String? = null
    @JvmField
    var status: Int = 0
    @JvmField
    var rate: Double = 0.0
    @JvmField
    var review: String? = null
    @JvmField
    var address: Address? = null

    val listProductsName: String?
        get() {
            if (products == null || products!!.isEmpty()) return ""
            var result = ""
            for (productOrder in products!!) {
                result += if (isEmpty(result)) {
                    productOrder.name
                } else {
                    ", " + productOrder.name
                }
            }
            return result
        }

    companion object {
        const val STATUS_NEW: Int = 1
        const val STATUS_DOING: Int = 2
        const val STATUS_ARRIVED: Int = 3
        const val STATUS_COMPLETE: Int = 4
    }
}
