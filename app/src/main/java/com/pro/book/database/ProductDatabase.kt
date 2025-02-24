package com.pro.book.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.pro.book.model.Product

@Database(entities = [Product::class], version = 1)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDAO(): ProductDAO?

    companion object {
        private const val DATABASE_NAME = "product.db"

        private var instance: ProductDatabase? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): ProductDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java, DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}
