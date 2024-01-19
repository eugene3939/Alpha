package com.example.alpha.ui.home

import ProductitemAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alpha.databinding.FragmentHomeBinding
import com.example.alpha.R
import com.example.alpha.ui.dbhelper.ProductDBHelper
import com.example.alpha.ui.myAdapter.ShopCartAdapter
import com.example.alpha.ui.myObject.ProductItem
import com.example.alpha.ui.myObject.ShopCart

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //product Table的項目
    private lateinit var productList: List<ProductItem>

    //儲存選擇的productItem位置
    private var selectedPositions = mutableSetOf<Int>()

    //新增List儲存篩選結果
    private var filteredProductList: List<ProductItem> = emptyList()

    //新增List儲存購物清單
    private lateinit var shoppingCart: ShopCart

    //總價購物車總價
    private var totalCartPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 ShoppingCart
        shoppingCart = ShopCart()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化 ShoppingCart
        shoppingCart = ShopCart()

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

        //從ProductTable放資料到productList
        getProductTable()

        //grTableData點擊事件
        binding.grTableData.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = filteredProductList[position]
            val productName = selectedProduct.pName
            Toast.makeText(requireContext(), "商品名稱: $productName", Toast.LENGTH_SHORT).show()

                // 提示用戶輸入數量
                showQuantityInputDialog { quantity ->
                    if (quantity>=0) {
                        // 用戶確認數量後，將商品及數量添加到購物車
                        shoppingCart.addProduct(selectedProduct, quantity)
                        selectedPositions.add(position)
                    }
                    //如果有包含數量0的shoppingCart則是直接刪除
                    shoppingCart.selectedProducts.removeIf { it.selectedQuantity == 0 }
                    // 更新 GridView 的外觀和購物車內容
                    updateGridViewAppearance()

                    //計算商品總價
                    var price = 0 //計算價格的區域變數(每次計算都先歸0)
                    for (i in shoppingCart.selectedProducts){   //計算總價要在這邊做，不要放到外面
                        price+= i.selectedQuantity*i.pPrice
                    }
                    totalCartPrice = price  //更新到全域變數
                    binding.txtTotalPrice.text="總價: ${totalCartPrice}元"
                }
        }

        // 讀取GridView的Adapter
        val adapter = ProductitemAdapter(productList, shoppingCart)
        binding.grTableData.adapter = adapter
        binding.grTableData.numColumns=productList.size

        //更新GridView顯示所在資料庫內容
        updateGridView(productList,null,null)

        //清除全選項目按鈕
        binding.btnClear.setOnClickListener {
            //清空搜尋結果
            filteredProductList = productList //搜尋結果回復
            binding.edtSearchRow.setText("")    //清空文字搜尋項目
            binding.edtSearchRow.setHint(R.string.txt_table)    //回復成預設搜尋文字

            updateGridView(productList,null,null)   //回復總表查詢
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

    //數量選擇
    private fun showQuantityInputDialog(callback: (Int) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.checknumber, null)

        builder.setView(dialogView)
        builder.setTitle("輸入數量")

        val quantityInput = dialogView.findViewById<EditText>(R.id.edt_click_num)

        builder.setPositiveButton("確定") { _, _ ->
            val userInput = quantityInput.text.toString()
            val quantity = userInput.toIntOrNull() ?: 1

            // 調用回調函數
            callback.invoke(quantity)
        }

        builder.setNegativeButton("取消") { _, _ ->
            // 用戶點擊取消，回調函數中的數量為零
            callback.invoke(0)
        }

        builder.show()
    }


    // 更新 GridView 的外觀
    private fun updateGridViewAppearance() {
        // 更新購物車的 GridView
        val adapterShop = ShopCartAdapter(shoppingCart.selectedProducts)
        binding.lsBuyChart.adapter = adapterShop

        // 更新商品列表的 GridView
        val adapter = ProductitemAdapter(filteredProductList,shoppingCart)
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
        val adapter = ProductitemAdapter(filteredProductList , shoppingCart)
        binding.grTableData.adapter = adapter
        binding.grTableData.numColumns = 2
    }

    //取得product table
    private fun getProductTable() {
        val databaseHelper = ProductDBHelper(requireContext())  //product Table Helper
        productList = databaseHelper.getAllProducts()   //取得product table items

        databaseHelper.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}