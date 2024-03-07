package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val paymentIds: String,
    val itemList: String,
    val totalPrice: Int,
    val discount: Int
)