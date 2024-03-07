package com.example.alpha.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.alpha.databinding.FragmentDashboardBinding

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
//        val invoiceList: MutableList<Invoice> = loadInvoices().toMutableList()

//        val adapter = InvoiceAdapter(invoiceList)
//        binding.lsInvoice.adapter = adapter

        // 設置 ListView 點擊事件
//        binding.lsInvoice.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            // 根據點擊的位置 position 獲取相關資訊
//            val clickedItem = invoiceList[position]
//
//            // 建立 AlertDialog 來顯示所點擊的資訊
//            val alertDialogBuilder = AlertDialog.Builder(requireContext())
//            alertDialogBuilder.setTitle("發票資訊")
//            alertDialogBuilder.setMessage("$clickedItem")
//            alertDialogBuilder.setPositiveButton("作廢") { dialog, _ ->
//                // 刪除資料庫中的發票
//                val invoiceRepository = InvoiceDBManager(requireContext())
//                CoroutineScope(Dispatchers.Main).launch {
//                    val deleteSuccess = invoiceRepository.deleteInvoice(clickedItem.id)
//                    if (deleteSuccess > 0) {
//                        // 從列表中移除被刪除的發票
//                        invoiceList.removeAt(position)
//                        adapter.notifyDataSetChanged()
//                    }
//                }
//
//                dialog.dismiss()
//            }.setNegativeButton("取消") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//            // 顯示 AlertDialog
//            val alertDialog = alertDialogBuilder.create()
//            alertDialog.show()
//        }

        return root
    }

//    private fun loadInvoices(): List<Invoice> {
//        val invoiceRepository = InvoiceDBManager(requireContext())
//        return invoiceRepository.getAllInvoices()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}