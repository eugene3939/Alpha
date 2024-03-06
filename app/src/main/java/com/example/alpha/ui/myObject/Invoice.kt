package com.example.alpha.ui.myObject

import androidx.room.Entity
import androidx.room.PrimaryKey

// 定義發票類別
@Entity(tableName = "InvoiceTable")
data class Invoice(
    @PrimaryKey
    val id: String,
    @JvmField
    val paymentIds: List<String>,
    val itemList: List<String>,
    val totalPrice: Int,
    val discount: Int
)



