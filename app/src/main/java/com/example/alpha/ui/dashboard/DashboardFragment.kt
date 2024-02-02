package com.example.alpha.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.example.alpha.databinding.FragmentDashboardBinding
import com.example.alpha.ui.dbhelper.InvoiceDBHelper
import com.example.alpha.ui.myAdapter.InvoiceAdapter
import com.example.alpha.ui.myObject.Invoice

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 加載並顯示發票數據
        val invoiceList : ArrayList<Invoice> = loadInvoices()

        val adapter = InvoiceAdapter(invoiceList)
        binding.lsInvoice.adapter = adapter

        // 設置 ListView 點擊事件
        binding.lsInvoice.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // 根據點擊的位置 position 獲取相關資訊
            val clickedItem = invoiceList[position]

            // 建立 AlertDialog 來顯示所點擊的資訊
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("發票資訊")
            alertDialogBuilder.setMessage("$clickedItem")
            alertDialogBuilder.setPositiveButton("作廢") { dialog, _ ->
                // 刪除資料庫中的發票
                val dbHelper = InvoiceDBHelper(requireContext())
                dbHelper.deleteInvoice(clickedItem.id) // 假設 clickedItem 有一個 id 屬性表示發票的唯一標識符

                // 從列表中移除被刪除的發票
                invoiceList.removeAt(position)
                adapter.notifyDataSetChanged()

                dialog.dismiss()
            }.setNegativeButton("取消"){dialog, _ ->
                dialog.dismiss()
            }

            // 顯示 AlertDialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        return root
    }

    private fun loadInvoices(): ArrayList<Invoice> {
        val dbHelper = InvoiceDBHelper(requireContext())

        return dbHelper.getAllInvoices()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}