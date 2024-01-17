package com.example.alpha.ui.myObject

class ShopCart {
    val selectedProducts: MutableList<ProductItem> = mutableListOf()

    fun addProduct(product: ProductItem) {
        selectedProducts.add(product)
    }

    fun removeProduct(product: ProductItem) {
        selectedProducts.remove(product)
    }

    fun clear() {
        selectedProducts.clear()
    }
}