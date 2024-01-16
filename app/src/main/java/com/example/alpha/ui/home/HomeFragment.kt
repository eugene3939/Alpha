package com.example.alpha.ui.home

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alpha.databinding.FragmentHomeBinding
import com.example.alpha.R
import com.example.alpha.ui.dbhelper.ProductDBHelper
import com.example.alpha.ui.dbhelper.TransactionDBHelper
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
            ViewModelProvider(this)[HomeViewModel::class.java]

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
                updateGridView(nowTableId,null)

                Log.d("目前所在的Table索引是", "索引: $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //更新GridView顯示所在資料庫內容
        updateGridView(nowTableId,null)

        return root
    }

    //顯示目前columns名稱
    private fun updateColumnNameShow(){
        val rowNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,nowColumns)
        binding.grColumnName.adapter = rowNameAdapter
        binding.grColumnName.numColumns = nowColumns.size
    }

    // 更新 GridView 中的資料
    private fun updateGridView(nowDBid: Int, searchColumns: String?) {
        // 清空先前的資料
        data.clear()
        // 獲取指定資料庫的資料
        getTableData(nowDBid,searchColumns)
        // 顯示在 GridView
        binding.grTableData.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
    }

    // 獲取指定資料庫的資料
    @SuppressLint("Range")
    private fun getTableData(nowDBid: Int,searchColumns: String?) {
        // 清空先前的資料
        data.clear()

        // 根據位置初始化資料庫
        when (nowDBid) {
            0 -> {
                // 初始化 User 資料庫
                val dbHelper = UserDBHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                nowColumns = resources.getStringArray(R.array.UserItems).toMutableList()

                // 根據需要執行對 User 資料庫的查詢並處理資料
                selectionData("UserTable",nowColumns,searchColumns)
            }
            1 -> {
                // 初始化 Product 資料庫
                val dbHelper = ProductDBHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                nowColumns = resources.getStringArray(R.array.ProductItems).toMutableList()

                // 根據需要執行對 Product 資料庫的查詢並處理資料
                selectionData("ProductTable",nowColumns,searchColumns)
            }
            else -> {
                // 初始化 Transaction 資料庫
                val dbHelper = TransactionDBHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                nowColumns = resources.getStringArray(R.array.TransactionItems).toMutableList()

                // 根據需要執行對 TransactionTable 的查詢並處理資料
                selectionData("TransactionTable",nowColumns,searchColumns)
            }
        }

        updateColumnNameShow()   //更新rowName
        //edtAutoFilling(nowColums)  //editText自動填詞
    }

    //依照欄位數、欄位名稱搜尋資料庫Select SQLite
    @SuppressLint("Range")
    private fun selectionData(tableName: String, columns: MutableList<String>, columnName: String?){
        Log.d("目前欄位", "$columns")

        // 清空先前的資料(欄位名稱+欄位資料)
        data.clear()

        // 將欄位名稱轉換成字串，用於構建SQL查詢語句
        val columnString = // 如果有指定 columnName，就只查詢單一欄位
            columnName ?: // 否則查詢所有欄位
            columns.joinToString(", ") // 將欄位名稱以逗號分隔

        // 執行查詢
        val query = "SELECT $columnString FROM $tableName;"
        val cursor = dbrw.rawQuery(query, null)

        // 檢查是否有查詢結果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 讀取每一列的資料
                if (columnName != null) {
                    val columnIndex = cursor.getColumnIndex(columnName)
                    data.add(cursor.getString(columnIndex))
                } else {
                    for (column in columns) {
                        val columnIndex = cursor.getColumnIndex(column)
                        data.add(cursor.getString(columnIndex))
                    }
                }
            } while (cursor.moveToNext())

            // 如果是搜尋部分欄位，更新 nowColumns
            if (columnName != null) {
                nowColumns.clear()
                nowColumns.add(columnName)
            }

            // 限制column
            binding.grTableData.numColumns = nowColumns.size
        } else {
            Toast.makeText(requireContext(), "Table還沒建立喔", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dbrw.close()    //關閉table容器
    }
}