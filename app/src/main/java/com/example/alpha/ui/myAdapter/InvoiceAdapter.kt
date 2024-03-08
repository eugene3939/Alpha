package com.example.alpha.ui.myAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.alpha.R
import com.example.alpha.ui.dbhelper.invoiceDao.Invoice
import java.util.Date

class InvoiceAdapter(private val dataList: MutableList<Invoice>?) : BaseAdapter() {
    override fun getCount(): Int {
        return dataList?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return dataList?.get(position) ?: Invoice(0, "No items", "No payment", 0, 0, Date())
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.invoice_item, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as Invoice
        viewHolder.bind(data)
        return view
    }

    class ViewHolder(itemView: View) {
        private val invoiceId: TextView = itemView.findViewById(R.id.txt_invoiceId)
        private val invoiceInfo: TextView = itemView.findViewById(R.id.txt_info)
        private val invoicePayment: TextView = itemView.findViewById(R.id.txt_totalPayment)

        @SuppressLint("SetTextI18n")
        fun bind(item: Invoice) {
            invoiceId.text = item.id.toString()
            invoiceInfo.text = item.itemList.replace(",", "\n")
            invoicePayment.text = item.paymentIds.replace(",", "\n")
        }
    }
}