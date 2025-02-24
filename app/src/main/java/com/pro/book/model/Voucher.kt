package com.pro.book.model

import com.pro.book.utils.Constant
import java.io.Serializable

class Voucher : Serializable {
    @JvmField
    var id: Long = 0
    @JvmField
    var discount: Int = 0
    @JvmField
    var minimum: Int = 0
    @JvmField
    var isSelected: Boolean = false

    constructor()

    constructor(id: Long, discount: Int, minimum: Int) {
        this.id = id
        this.discount = discount
        this.minimum = minimum
    }

    val title: String
        get() = "Giảm giá $discount%"

    val minimumText: String
        get() {
            if (minimum > 0) {
                return "Áp dụng cho đơn hàng tối thiểu " + minimum + Constant.CURRENCY
            }
            return "Áp dụng cho mọi đơn hàng"
        }

    fun getCondition(amount: Int): String {
        if (minimum <= 0) return ""
        val condition = minimum - amount
        if (condition > 0) {
            return "Hãy mua thêm " + condition + Constant.CURRENCY + " để nhận được khuyến mại này"
        }
        return ""
    }

    fun isVoucherEnable(amount: Int): Boolean {
        if (minimum <= 0) return true
        val condition = minimum - amount
        return condition <= 0
    }

    fun getPriceDiscount(amount: Int): Int {
        return (amount * discount) / 100
    }
}