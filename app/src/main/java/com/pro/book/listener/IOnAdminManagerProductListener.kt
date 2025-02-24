package com.pro.book.listener

import com.pro.book.model.Product

interface IOnAdminManagerProductListener {
    fun onClickUpdateProduct(product: Product)
    fun onClickDeleteProduct(product: Product)
}
