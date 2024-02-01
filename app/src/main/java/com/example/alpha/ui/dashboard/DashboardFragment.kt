package com.example.alpha.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

//        for (i in invoiceList){
//            Log.d("包含: ","$i")
//        }

        val adapter = InvoiceAdapter(invoiceList)
        binding.lsInvoice.adapter = adapter

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