package com.example.alpha.ui.home

import ProductitemAdapter
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alpha.databinding.FragmentHomeBinding
import com.example.alpha.R
import com.example.alpha.ui.dbhelper.ProductDBHelper
import com.example.alpha.ui.myObject.ProductItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //product容器
    private lateinit var dbrw: SQLiteDatabase

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

        // 初始化商品資料庫
        val dbHelper = ProductDBHelper(requireContext())
        dbrw = dbHelper.writableDatabase

        //下拉式選單顯示全部的product種類
        val productTypes = resources.getStringArray(R.array.productType)  //全部的商品種類
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,productTypes)
        binding.spProductType.adapter = spinnerAdapter

        //下拉式選單變更選擇的資料庫
        binding.spProductType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //更新所在資料庫索引
                //productTypeId = position

                val selectedItem = parent?.getItemAtPosition(position)    //取得選擇的資料
                //更新GridView顯示所在資料庫內容
                //updateGridView("ProductTable",selectedItem)

                Log.d("目前所在的Table索引是", "索引: $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // 創建一個包含 ProductItem 物件的測試集合
        val productList = listOf(
            ProductItem(1,R.drawable.ic_hello, "Product 1", "Type 1", 10, 29.99),
            ProductItem(2,R.drawable.ic_hello, "Product 2", "Type 2", 20, 39.99),
            ProductItem(3,R.drawable.ic_hello, "Product 3", "Type 3", 15, 49.99)
        )

        // 讀取GridView的Adapter
        val adapter = ProductitemAdapter(productList)
        binding.grTableData.adapter = adapter
        binding.grTableData.numColumns=2

        //更新GridView顯示所在資料庫內容
        //updateProductShow("ProductTable","all")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dbrw.close()    //關閉table容器
    }
}