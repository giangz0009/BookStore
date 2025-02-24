package com.pro.book.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pro.book.model.Product

@Dao
interface ProductDAO {
    @Insert
    fun insertProduct(product: Product)

    @get:Query("SELECT * FROM product")
    val listProductCart: MutableList<Product>?

    @Query("SELECT * FROM product WHERE id=:id")
    fun checkProductInCart(id: Long): List<Product>?

    @Delete
    fun deleteProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Query("DELETE from product")
    fun deleteAllProduct()
}
