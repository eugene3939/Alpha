package com.example.alpha.ui.myAdapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.alpha.R
import com.example.alpha.ui.myObject.Invoice

class InvoiceAdapter(private val dataList: List<Invoice>) : BaseAdapter() {
    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.invoice_item, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as Invoice
        viewHolder.bind(data)

        for (i in dataList){
            Log.d("咚咚: ", i.toString())
        }

        return view
    }

    class ViewHolder(itemView: View) {
        //請見product_item.xml
        private val invoiceId: TextView = itemView.findViewById(R.id.txt_invoiceId)
        private val invoiceInfo: TextView = itemView.findViewById(R.id.txt_info)
        private val invoicePayment: TextView = itemView.findViewById(R.id.txt_totalPayment)
        @SuppressLint("SetTextI18n")
        fun bind(item: Invoice) {
            invoiceId.text = item.id
            if (item.itemList.isNotEmpty()) {
                invoiceInfo.text = item.paymentIds.joinToString("") {it}
            } else {
                invoiceInfo.text = "No items"
            }
            invoicePayment.text = item.discount.toString()
        }
    }
}