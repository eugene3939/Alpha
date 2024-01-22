package com.example.alpha.ui.myAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.alpha.R
import com.example.alpha.ui.myObject.ProductItem

class ShopCartAdapter(private val dataList: List<ProductItem>) : BaseAdapter() {

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
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.buychart, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as ProductItem
        viewHolder.bind(data)

        return view
    }

    class ViewHolder(itemView: View) {
        //請見buyChart.xml
        private val shopCartName: TextView = itemView.findViewById(R.id.txt_buyChart)
        private val shopCartNumber: TextView = itemView.findViewById(R.id.txt_buyNumber)
        private val shopCartPrice: TextView = itemView.findViewById(R.id.txt_buyPrice)
        private val shopCartSimplePrice: TextView = itemView.findViewById(R.id.txt_buySimplePrice)

        @SuppressLint("SetTextI18n")
        fun bind(shop: ProductItem) {
            shopCartName.text = shop.pName
            shopCartNumber.text = "${shop.selectedQuantity}x"
            shopCartPrice.text = "${shop.pPrice}="
            shopCartSimplePrice.text = "${shop.selectedQuantity*shop.pPrice}"
        }
    }
}
