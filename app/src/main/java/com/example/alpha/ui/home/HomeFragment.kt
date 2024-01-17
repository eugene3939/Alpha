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
import android.widget.Toast
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

    //product Table的項目
    private lateinit var productList: List<ProductItem>

    //儲存選擇的productItem位置
    private var selectedPositions = mutableSetOf<Int>()

    // 新增一個變數來儲存篩選結果
    private var filteredProductList: List<ProductItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                if (selectedItem=="all"){   //查找所有資料
                    updateGridView(productList,null,null)
                }else{
                    updateGridView(productList,"pType",selectedItem)
                }

                Log.d("目前所在的Table索引是", "索引: $selectedItem")

                selectedPositions.clear()
                updateGridViewAppearance()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

//        // 創建一個包含 ProductItem 物件的測試集合
//        val productList = listOf(
//            ProductItem(1,R.drawable.ic_hello, "可樂", "飲料", 200, 30),
//            ProductItem(2,R.drawable.ic_hello, "薯條", "炸物", 150, 45),
//            ProductItem(3,R.drawable.ic_hello, "牛肉堡", "漢堡", 100, 150)
//        )

        //從ProductTable更新productList
        getProductTable()

        //grTableData點擊事件
        binding.grTableData.setOnItemClickListener { parent, view, position, id ->
            //顯示點擊項目名稱
            val selectedProduct = productList[position]
            val productName = selectedProduct.pName
            Toast.makeText(requireContext(), "商品名稱: $productName", Toast.LENGTH_SHORT).show()

            // 切換選擇狀態
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position)
            } else {
                selectedPositions.add(position)
            }

            // 更新 GridView 的外觀
            updateGridViewAppearance()
        }

        // 讀取GridView的Adapter
        val adapter = ProductitemAdapter(productList, selectedPositions)
        binding.grTableData.adapter = adapter
        binding.grTableData.numColumns=2

        //更新GridView顯示所在資料庫內容
        updateGridView(productList,null,null)

        //清除全選項目按鈕
        binding.btnClear.setOnClickListener {
            //清空搜尋結果
            filteredProductList = productList
            updateGridView(filteredProductList,null,null)
            //清空點擊項目
//            selectedPositions.clear()
//            updateGridViewAppearance()
        }

        //單一欄位查詢
        binding.btnSearch.setOnClickListener { //先搜尋品名為主(數值型依狀況添加)
            val searchText = binding.edtSearchRow.text.toString()
            if (searchText!=""){
                updateGridView(productList,"PName",searchText)
            }
        }

        return root
    }

    // 更新 GridView 的外觀
    private fun updateGridViewAppearance() {
        val adapter = ProductitemAdapter(filteredProductList, selectedPositions)
        binding.grTableData.adapter = adapter
    }

    //查詢特定column
    private fun updateGridView(productList: List<ProductItem>, columnName: String?, selectedItem: Any?) {
//         如果 columnName 和 selectedItem 不為空，則篩選商品列表
        filteredProductList  = if (columnName != null && selectedItem != null) {
            val selectedValue = selectedItem.toString()
            //productList.filter { getColumnValue(it, columnName) == selectedValue }    //從list撈篩選

            val dbHelper = ProductDBHelper(requireContext())
            dbHelper.getProductsByCondition(columnName,selectedValue)   //從table篩選
        } else {
            // 如果 columnName 或 selectedItem 為空，或 selectedItem 不是預期的型態，保持原始列表
            productList
        }

        // 讀取 GridView 的 Adapter
        val adapter = ProductitemAdapter(filteredProductList , selectedPositions)
        binding.grTableData.adapter = adapter
        binding.grTableData.numColumns = 2
    }

    //取得product table
    private fun getProductTable() {
        val databaseHelper = ProductDBHelper(requireContext())  //product Table Helper
        productList = databaseHelper.getAllProducts()   //取得product table items

        //取得資料庫的物件到productList
        for (productItem in productList) {
            // 在這裡使用 productItem，例如顯示它或進行其他處理
            Log.d("Product", "ID: ${productItem.pId}, Name: ${productItem.pName}")
        }

        databaseHelper.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}