package com.example.alpha.ui.myObject

import androidx.room.Entity

// 定義發票類別
@Entity(tableName = "InvoiceTable")
data class Invoice(val id: String, val paymentIds: List<String>, val itemList: List<String>, val totalPrice: Int, val discount: Int)




