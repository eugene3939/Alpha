package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Merchandises")

data class Merchandise(
    @PrimaryKey(autoGenerate = true)
    val pId: Int = 0,
    val imageUrl: Int, val pName: String,
    val pType: String, val pBarcode: String,
    val pNumber: Int, val pPrice: Int,
    var selectedQuantity: Int = 0,   //購物車選擇數量
)