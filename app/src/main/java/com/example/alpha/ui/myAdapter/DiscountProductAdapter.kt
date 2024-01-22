package com.example.alpha.ui.myAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.alpha.R
import com.example.alpha.ui.myObject.DiscountedProduct
import com.example.alpha.ui.myObject.ProductItem

class DiscountProductAdapter(private val dataList: List<DiscountedProduct>, private val productData: List<ProductItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.discount_product, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as DiscountedProduct

        // 在這裡獲取商品對應的價格
        val productPrice = getProductPrice(data.pId)

        viewHolder.bind(data, productPrice)
        return view
    }

    class ViewHolder(itemView: View){
        // 請見discount_product.xml
        private val shopCartName: TextView = itemView.findViewById(R.id.txt_discountName)
        private val shopCartNumber: TextView = itemView.findViewById(R.id.txt_discountNumber)
        private val shopCartPrice: TextView = itemView.findViewById(R.id.txt_discountTotal)

        @SuppressLint("SetTextI18n")
        fun bind(items: DiscountedProduct, productPrice: Int?) {
            shopCartName.text = items.pDescription
            shopCartNumber.text = "-${items.selectedQuantity} x "

            if (items.pDiscount > 0.0 && productPrice != null && items.pDiscount != null) {
                // 顯示 items.pDiscount * items.selectedQuantity * productPrice
                val totalDiscount = ((1-items.pDiscount) * items.selectedQuantity * productPrice).toInt()
                shopCartPrice.text = "-${items.pDiscount} * $productPrice = -$totalDiscount"
            } else {
                // 沒有單一折扣顯示折價額度
                shopCartPrice.text = "${items.pChargebacks} = -${items.selectedQuantity * items.pChargebacks}"
            }
        }
    }

    // 獲取商品對應的價格
    private fun getProductPrice(productId: Int): Int? {
        val product = productData.find { it.pId == productId }
        return product?.pPrice
    }
}
