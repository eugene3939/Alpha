package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DiscountMerchandises")

//折扣商品清單(折扣單)
data class DiscountMerchandise(
    @PrimaryKey(autoGenerate = true)
    val pId: Int = 0,          //單品編號
    val pDescription: String,  //單品優惠描述
    val pDiscount: Double,     //單品折扣
    val pChargebacks: Int,     //單品折讓
    val selectedQuantity: Int  //單品選擇數量
)