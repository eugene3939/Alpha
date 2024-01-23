package com.example.alpha.ui.myObject

import android.util.Log

class DiscountCalculator(private val productData: List<ProductItem>) {

    private val discountInfoList = mutableListOf<DiscountInfo>()

    fun calculateDiscount(item: DiscountedProduct): String {
        val productPrice = getProductPrice(item.pId)

        var totalDiscount = 0   // 單項折價總和
        val discountInfo: DiscountInfo

        if (item.pDiscount > 0.0 && productPrice != null && item.pDiscount != null) {
            totalDiscount = (item.pDiscount * item.selectedQuantity * productPrice).toInt()

            discountInfo = DiscountInfo(item.pDescription, item.pId, item.selectedQuantity, totalDiscount)
            Log.d("你好: ","$discountInfo")
        } else {
            totalDiscount = item.selectedQuantity * item.pChargebacks

            discountInfo = DiscountInfo(item.pDescription, item.pId, item.selectedQuantity, totalDiscount)
            Log.d("你好: ","$discountInfo")
        }

        discountInfoList.add(discountInfo)

        // 將 return 語句放在最後
        return if (item.pDiscount > 0.0 && productPrice != null && item.pDiscount != null) {
            "${item.pDiscount} x $productPrice = -${discountInfo.totalDiscount}"
        } else {
            "${item.pChargebacks} = -${discountInfo.totalDiscount}"
        }
    }

    private fun getProductPrice(productId: Int): Int? {
        val product = productData.find { it.pId == productId }
        return product?.pPrice
    }

    fun getDiscountInfoList(): List<DiscountInfo> {
        return discountInfoList.toList()
    }
}
