package com.pro.book.listener

import com.pro.book.model.Product

fun interface IClickProductListener {
    fun onClickProductItem(product: Product)
}
