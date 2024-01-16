package com.example.alpha.ui.home

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alpha.databinding.FragmentHomeBinding
import com.example.alpha.R
import com.example.alpha.ui.dbhelper.UserDBHelper

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //product容器
    private lateinit var dbrw: SQLiteDatabase
    //table的欄位名稱
    private var nowColumns: MutableList<String> = mutableListOf()
    // 在類別內部宣告一個空的List<String>用來存放欄位名稱
    private val data: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // 初始化商品資料庫
        val dbHelper = UserDBHelper(requireContext())
        dbrw = dbHelper.writableDatabase

        //預設資料庫索引為User (0)
        var nowTableId = 0

        //下拉式選單顯示全部的table種類
        val tablesArray = resources.getStringArray(R.array.tableNames)  //全部的table種類
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,tablesArray)
        binding.spTableName.adapter = spinnerAdapter

        //下拉式選單變更選擇的資料庫
        binding.spTableName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //更新所在資料庫索引
                nowTableId = position

                val selectedItem = parent?.getItemAtPosition(position)    //取得選擇的資料
                //更新GridView顯示所在資料庫內容
                //updateGridView(nowDBid,null)

                Log.d("目前所在的Table索引是", "索引: $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}