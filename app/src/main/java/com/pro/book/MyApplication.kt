package com.pro.book

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pro.book.prefs.DataStoreManager

class MyApplication : Application() {
    private lateinit var mFirebaseDatabase: FirebaseDatabase

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL)
        DataStoreManager.init(applicationContext)
    }

    val adminDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("admin")

    val voucherDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("voucher")

    val addressDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("address")

    val categoryDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("category")

    val productDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("product")

    fun getProductDetailDatabaseReference(productId: Long): DatabaseReference {
        return mFirebaseDatabase.getReference("product/$productId")
    }

    val feedbackDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("/feedback")

    val orderDatabaseReference: DatabaseReference
        get() = mFirebaseDatabase.getReference("order")

    fun getRatingProductDatabaseReference(productId: String): DatabaseReference {
        return mFirebaseDatabase.getReference("/product/$productId/rating")
    }

    fun getOrderDetailDatabaseReference(orderId: Long): DatabaseReference {
        return mFirebaseDatabase.getReference("order/$orderId")
    }

    companion object {
        private const val FIREBASE_URL = "https://bookstore-32932-default-rtdb.firebaseio.com"
        @JvmStatic
        fun get(context: Context): MyApplication {
            return context.applicationContext as MyApplication
        }
    }
}
