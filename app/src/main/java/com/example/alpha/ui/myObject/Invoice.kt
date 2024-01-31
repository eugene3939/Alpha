package com.example.alpha.ui.myObject

// 定義發票類別
data class Invoice(val id: String, val paymentIds: List<String>, val itemList: List<String>, val totalPrice: Int, val discount: Int)




