package com.example.alpha.ui.myObject

data class ProductItem(
    val pId: Int,
    val imageUrl: Int, val pName: String,
    val pType: String, val pBarcode: String,
    val pNumber: Int, val pPrice: Int,
    var selectedQuantity: Int = 0,   //購物車選擇數量
)