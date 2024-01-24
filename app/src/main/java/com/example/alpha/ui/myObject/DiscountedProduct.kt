package com.example.alpha.ui.myObject

data class DiscountedProduct(val pId: Int,              //單品編號
                             val pDescription: String,  //單品優惠描述
                             val pDiscount: Double,     //單品折扣
                             val pChargebacks: Int,     //單品折讓
                             val selectedQuantity: Int  //單品選擇數量
)
