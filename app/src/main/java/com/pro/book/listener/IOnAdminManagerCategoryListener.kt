package com.pro.book.listener

import com.pro.book.model.Category

interface IOnAdminManagerCategoryListener {
    fun onClickUpdateCategory(category: Category)
    fun onClickDeleteCategory(category: Category)
    fun onClickItemCategory(category: Category)
}
