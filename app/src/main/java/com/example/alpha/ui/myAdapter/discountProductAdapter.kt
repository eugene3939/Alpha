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

class discountProductAdapter(private val dataList: List<DiscountedProduct>) : BaseAdapter() {
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
        viewHolder.bind(data)

        return view
    }

    class ViewHolder(itemView: View) {
        // 請見discount_product.xml
        private val shopCartName: TextView = itemView.findViewById(R.id.txt_discountName)
        private val shopCartNumber: TextView = itemView.findViewById(R.id.txt_discountNumber)
        private val shopCartPrice: TextView = itemView.findViewById(R.id.txt_discountTotal)

        @SuppressLint("SetTextI18n")
        fun bind(items: DiscountedProduct) {
            shopCartName.text = items.pDescription
            shopCartNumber.text = "${items.selectedQuantity}x"
            shopCartPrice.text = "${items.pDiscount}="
        }
    }
}
