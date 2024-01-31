package com.example.alpha.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alpha.databinding.FragmentDashboardBinding
import com.example.alpha.ui.dbhelper.InvoiceDBHelper
import com.example.alpha.ui.myAdapter.InvoiceAdapter
import com.example.alpha.ui.myObject.Invoice

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var adapter: InvoiceAdapter // 需要自定義的適配器
    private lateinit var allInvoices: ArrayList<Invoice> // 將allInvoices轉為全局變量

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listView: ListView = binding.lsInvoice
        adapter = InvoiceAdapter(requireContext())
        listView.adapter = adapter

        // 加載並顯示發票數據
        loadInvoiceData()

        return root
    }

    private fun loadInvoiceData() {
        // 從數據庫中獲取所有發票數據，並將其添加到適配器中
        val dbHelper = InvoiceDBHelper(requireContext())
        allInvoices = dbHelper.getAllInvoices() // 初始化全局變量allInvoices
        adapter.setData(allInvoices)

        // 檢查 InvoiceDBHelper 的所有項目
        for (invoice in allInvoices) {
            Log.d("Invoice Details", "ID: ${invoice.id}, Payment IDs: ${invoice.paymentIds}, Item List: ${invoice.itemList}, Total Price: ${invoice.totalPrice}, Discount: ${invoice.discount}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}