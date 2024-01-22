package com.example.alpha.ui.myObject

data class DiscountedProduct(val pId: Int,
                             val pDescription: String,
                             val pDiscount: Double,
                             val pChargebacks: Int,
                             val pClusterItem: String?,
                             val selectedQuantity: Int //單品選擇數量
)
