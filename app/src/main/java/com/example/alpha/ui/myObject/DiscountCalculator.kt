package com.example.alpha.ui.myObject

class DiscountCalculator(private val productData: List<ProductItem>) {

    fun calculateDiscount(item: DiscountedProduct): String {
        val productPrice = getProductPrice(item.pId)
        return if (item.pDiscount > 0.0 && productPrice != null && item.pDiscount != null) {
            val totalDiscount = (item.pDiscount * item.selectedQuantity * productPrice).toInt()
            "-${item.pDiscount} * $productPrice = -$totalDiscount"
        } else {
            "${item.pChargebacks} = -${item.selectedQuantity * item.pChargebacks}"
        }
    }

    private fun getProductPrice(productId: Int): Int? {
        val product = productData.find { it.pId == productId }
        return product?.pPrice
    }
}
