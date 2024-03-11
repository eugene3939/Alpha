package com.example.alpha.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.alpha.databinding.FragmentDashboardBinding
import com.example.alpha.ui.dbhelper.invoiceDao.Invoice
import com.example.alpha.ui.dbhelper.invoiceDao.InvoiceDBManager
import com.example.alpha.ui.myAdapter.InvoiceAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseManager: InvoiceDBManager //(用封裝的方式獲取Dao)

    private var invoiceList: MutableList<Invoice>? = null //發票清單

    // 加載並顯示發票數據
    private val adapter: InvoiceAdapter by lazy {
        InvoiceAdapter(invoiceList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化資料庫管理器
        databaseManager = InvoiceDBManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO){
            invoiceList = databaseManager.getAllInvoicesTable()//更新發票清單

            // 在主執行緒上更新UI
            withContext(Dispatchers.Main) {
                binding.lsInvoice.adapter = adapter
            }

            //有資料進行發票內容檢查
            if (invoiceList!=null){
                for (i in invoiceList!!){
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Taipei")

                    // 將購買時間轉換為日期字符串
                    val purchaseTimeFormatted = dateFormat.format(i.purchaseTime)

                    // 輸出日期字符串
                    println("發票日期: $purchaseTimeFormatted")
                }
            }
        }

        // 設置 ListView 點擊事件
        binding.lsInvoice.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // 根據點擊的位置 position 獲取相關資訊
            val clickedItem = invoiceList?.get(position)

            // 建立 AlertDialog 來顯示所點擊的資訊
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("發票資訊")
            alertDialogBuilder.setMessage("$clickedItem")
            alertDialogBuilder.setPositiveButton("作廢") { dialog, _ ->
                // 刪除資料庫中的發票
                val invoiceRepository = InvoiceDBManager(requireContext())
                lifecycleScope.launch(Dispatchers.IO){
                    clickedItem?.let { invoiceRepository.deleteById(it.id) }

                    // 從列表中移除被刪除的發票
                    invoiceList?.removeAt(position)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }

                dialog.dismiss()
            }.setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }

            // 顯示 AlertDialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}