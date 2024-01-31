package com.example.alpha.ui.myAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.alpha.R
import com.example.alpha.databinding.InvoiceItemBinding
import com.example.alpha.ui.myObject.Invoice

class InvoiceAdapter(private val context: Context) : BaseAdapter() {
    private var invoiceList = listOf<Invoice>()

    fun setData(data: List<Invoice>) {
        invoiceList = data
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return invoiceList.size
    }

    override fun getItem(position: Int): Any {
        return invoiceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: InvoiceItemBinding
        val view: View

        if (convertView == null) {
            binding = InvoiceItemBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as InvoiceItemBinding
            view = convertView
        }

        val invoice = invoiceList[position]
        binding.txtInvoiceId.text = invoice.id
        // 设置其他视图的内容

        return view
    }
}