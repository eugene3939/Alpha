package com.example.alpha.ui.myAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.alpha.R
import com.example.alpha.ui.myObject.ProductItem
import com.example.alpha.ui.myObject.ShopCart

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

    // 在 getView 方法中更新相應的背景顏色
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.buychart, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as ProductItem
        viewHolder.bind(data)

//        // 檢查該位置是否被選擇，並設置相應的背景顏色
//        if (selectedPositions.contains(position)) {
//            // 被選擇時的背景顏色
//            view.setBackgroundResource(R.color.purple_200)
//        } else {
//            // 正常狀態的背景顏色
//            view.setBackgroundResource(R.color.white)
//        }

        return view
    }

    class ViewHolder(itemView: View) {
        private val shopCartName: TextView = itemView.findViewById(R.id.bxt_buyChart)
        fun bind(shop: ProductItem) {
            shopCartName.text = shop.pName
        }
    }
}
