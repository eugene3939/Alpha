package com.example.alpha.ui.myAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.alpha.R
import com.example.alpha.ui.myObject.DiscountInfo

class DiscountProductAdapter(private val dataList: List<DiscountInfo>) : BaseAdapter() {
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
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.discount_product, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as DiscountInfo
        viewHolder.bind(data)

        return view
    }

    class ViewHolder(itemView: View) {
        private val shopCartName: TextView = itemView.findViewById(R.id.txt_discountName)
        private val shopCartNumber: TextView = itemView.findViewById(R.id.txt_discountNumber)
        private val shopCartTotalPrice: TextView = itemView.findViewById(R.id.txt_discountTotal)

        @SuppressLint("SetTextI18n")
        fun bind(items: DiscountInfo) {
            shopCartName.text = items.discountDescription
            shopCartNumber.text = "x ${items.selectedQuantity} "
            shopCartTotalPrice.text = "- ${items.totalDiscount}"
        }
    }
}
