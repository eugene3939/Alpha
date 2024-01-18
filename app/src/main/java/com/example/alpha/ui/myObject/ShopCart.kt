package com.example.alpha.ui.myObject

class ShopCart {
    val selectedProducts: MutableList<ProductItem> = mutableListOf()

    fun addProduct(product: ProductItem, quantity: Int) {
        val existingProduct = findProductById(selectedProducts, product.pId)

        if (existingProduct != null) {
            // 商品已在購物車中，更新數量
            existingProduct.selectedQuantity = quantity
        } else {
            // 商品不在購物車中，添加到購物車
            val productCopy = product.copy(selectedQuantity = quantity)
            selectedProducts.add(productCopy)
        }
    }

    fun removeProduct(product: ProductItem) {
        selectedProducts.remove(product)
    }

    fun clear() {
        selectedProducts.clear()
    }

    fun findProductById(products: List<ProductItem>, productId: Int): ProductItem? {
        return products.find { it.pId == productId }
    }
}