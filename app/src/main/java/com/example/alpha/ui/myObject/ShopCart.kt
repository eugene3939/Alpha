package com.example.alpha.ui.myObject

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.example.alpha.R


class ShopCart {
    val selectedProducts: MutableList<ProductItem> = mutableListOf()

    fun addProduct(product: ProductItem, quantity: Int) {
        val existingProduct = findProductById(selectedProducts, product.pId)

        if (existingProduct != null) {
            // 商品已在購物車中，更新數量
            existingProduct.selectedQuantity += quantity
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

    fun showQuantityInputDialog(context: Context, onQuantityEntered: (Int) -> Unit) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.checknumber, null)

        val quantityEditText: EditText = dialogView.findViewById(R.id.edt_click_num)

        builder.setView(dialogView)
            .setTitle("Enter Quantity")
            .setPositiveButton("OK") { _, _ ->
                val quantityStr = quantityEditText.text.toString()
                if (quantityStr.isNotBlank()) {
                    val quantity = quantityStr.toInt()
                    onQuantityEntered(quantity)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }
}