package com.example.alpha.ui.myObject

import java.io.Serializable


data class DiscountInfo(val discountDescription: String, val productId: String, val selectedQuantity: Int, var totalDiscount: Int):
    Serializable
