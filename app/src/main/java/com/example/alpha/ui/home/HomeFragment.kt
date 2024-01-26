package com.example.alpha.ui.home

import ProductItemAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alpha.databinding.FragmentHomeBinding
import com.example.alpha.R
import com.example.alpha.ui.Payment
import com.example.alpha.ui.dbhelper.DiscountDBHelper
import com.example.alpha.ui.dbhelper.PairDiscountDBHelper
import com.example.alpha.ui.dbhelper.ProductDBHelper
import com.example.alpha.ui.myAdapter.ShopCartAdapter
import com.example.alpha.ui.myAdapter.DiscountProductAdapter
import com.example.alpha.ui.myObject.DiscountInfo
import com.example.alpha.ui.myObject.DiscountedProduct
import com.example.alpha.ui.myObject.ProductItem
import com.example.alpha.ui.myObject.ShopCart
import java.io.Serializable

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //product Table的項目(總表)
    private lateinit var productList: List<ProductItem>
    //新增List儲存篩選結果(依據文字搜尋或欄位搜尋結果)
    private var filteredProductList: List<ProductItem> = emptyList()
    //儲存折扣項目(描述、id、數量、價格)
    private val discountInfoList = mutableListOf<DiscountInfo>()

    //儲存選擇的productItem位置
    private var selectedPositions = mutableSetOf<Int>()

    //新增List儲存購物清單
    private lateinit var shoppingCart: ShopCart
    //購物車總價
    private var totalCartPrice = 0

    //總折扣額度
    private var totalDiscount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 ShoppingCart
        shoppingCart = ShopCart()
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //下拉式選單顯示全部的product種類
        val productTypes = resources.getStringArray(R.array.productType)  //全部的商品種類
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,productTypes)
        binding.spProductType.adapter = spinnerAdapter

        //下拉式選單變更選擇的Table
        binding.spProductType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedItem = parent?.getItemAtPosition(position)    //取得選擇的資料
                //更新GridView顯示所在資料庫內容
                when (selectedItem) {
                    "all" -> {   //查找所有資料
                        updateGridView(productList,null,null)   //回復總表查詢
                    }
                    else -> {
                        updateGridView(productList,"pType",selectedItem)
                    }
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

        //grTableData點擊事件(選擇商品、數量)
        binding.grTableData.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = filteredProductList[position]
            val productName = selectedProduct.pName
            Toast.makeText(requireContext(), "商品名稱: $productName", Toast.LENGTH_SHORT).show()

            discountInfoList.clear()    //每次讀取都清除之前的購物車折價清單

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

                    //確認折扣
                    val discountProducts = checkDiscount(shoppingCart.selectedProducts)

                    //取得總折扣額度
                    totalDiscount = getTotalDiscount(discountProducts)

                    Log.d("顯示折扣: ","$totalDiscount")

                    //顯示各個商品discountInfoList的資訊(折扣清單)
                    for (i in discountInfoList){
                        Log.d("各項商品詳細折價資訊","$i")
                  }

                    //計算商品總價
                    var price = 0 //計算價格的區域變數(每次計算都先歸0)
                    for (i in shoppingCart.selectedProducts){   //計算總價要在這邊做，不要放到外面
                        price+= i.selectedQuantity * i.pPrice
                    }

                    totalCartPrice = price  //更新總價到全域變數

                    binding.txtTotalPrice.text="總價: $totalCartPrice - $totalDiscount = ${totalCartPrice-totalDiscount}元"
                }
        }

        // 讀取GridView的Adapter
        val adapter = ProductItemAdapter(productList, shoppingCart)
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

        //單一欄位查詢 (能依據名稱、商品編號、商品貨號查詢)
        binding.btnSearch.setOnClickListener {
            val searchText = binding.edtSearchRow.text.toString()
            if (searchText.isNotBlank()) {
                val dbHelper = ProductDBHelper(requireContext())
                filteredProductList = dbHelper.getProductsByKeyword(searchText)
                updateGridView(filteredProductList, null, null)
                dbHelper.close()
            }
        }

        //送出之前先讓用戶檢查購物車
        binding.btnDeal.setOnClickListener {
            //顯示所有購物車項目，並引導使用者到付款頁面(這邊用AlertDialog檢查)
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
            val dialogView = inflater.inflate(R.layout.purchase_confirm, null)

            // 顯示購物車的ListView
            val shopCartList = dialogView.findViewById<ListView>(R.id.buyChart_content)
            val adapterShop = ShopCartAdapter(shoppingCart.selectedProducts)
            shopCartList.adapter = adapterShop

            val discountItemList = dialogView.findViewById<ListView>(R.id.buyChart_discount)
            val adapterDiscount = DiscountProductAdapter(discountInfoList)
            discountItemList.adapter = adapterDiscount

            //顯示總價
            val  shopTotalPrice = dialogView.findViewById<TextView>(R.id.buyChart_totalPrice)
            shopTotalPrice.text = "總價: $totalCartPrice - $totalDiscount = ${totalCartPrice-totalDiscount}"

            builder.setView(dialogView)
            builder.setTitle("購買項目")

            builder.setPositiveButton("確定") { _, _ ->
                //確認是否購物車為空，為空則跳出通知
                if (shoppingCart.selectedProducts.size==0){
                    Toast.makeText(requireContext(),"你沒有選擇商品喔",Toast.LENGTH_SHORT).show()
                }else{
                    //送出商品
                    Toast.makeText(requireContext(),"前往付款頁面",Toast.LENGTH_SHORT).show()

                    //送出用戶選擇的商品資料到付款頁面
                    val intent = Intent(requireContext(), Payment::class.java)
                    intent.putExtra("shoppingCart",shoppingCart)
                    intent.putExtra("discountInfoList", discountInfoList as Serializable)
                    startActivity(intent)
                }

                //要傳遞購物清單、折扣資訊
//                for (i in shoppingCart.selectedProducts){
//                    Log.d("你好","$i")
//                }
//                for (j in discountInfoList){
//                    Log.d("購物清單","$j")
//                }
            }
            builder.setNegativeButton("取消") { dialog, _ ->
                // 用戶點擊取消
                dialog.dismiss()
            }

            builder.show()
            //商品庫存等交易都確定後才扣除
        }

        return root
    }

    //取得總折扣金額
    private fun getTotalDiscount(discountProducts: List<DiscountedProduct>): Int {
        val dbHelper = ProductDBHelper(requireContext())

        var totalDiscount = 0

        for (i in discountProducts) {
            // 取得購物車項目中的商品價格
            val singleItem = dbHelper.getProductsByCondition("pId", i.pId.toString())   // 找出與購物車對應的商品

            var sum = 0   // 總折扣金額
            var permission = false // 確認是否能加入清單

            //現金折扣
            if (i.pDiscount > 0.0) {   // 單一商品折扣
                sum += (i.pDiscount * i.selectedQuantity * singleItem[0].pPrice).toInt()
                permission = true
            }

            //現金折讓
            if (i.pChargebacks> 0){ //單一商品折讓
                sum = i.pChargebacks * i.selectedQuantity
                permission = true
            }

            //存在折扣，加入折扣資訊
            if (permission) {    // 可加入的情況
                // 創建 DiscountInfo 物件
                val discountInfo = DiscountInfo(
                    discountDescription = i.pDescription,
                    productId = i.pId,
                    selectedQuantity = i.selectedQuantity,
                    totalDiscount = sum
                )
                // 將 DiscountInfo 加入到列表中
                discountInfoList.add(discountInfo)

                // 判斷是否是組合商品，如果是，處理組合商品的邏輯
                if (isPairDiscount(i.pId)) {
                    handlePairDiscount(i.pId)
                }
            }
            totalDiscount += sum    //加入單向折扣到總折扣
        }

        //組合折扣

        return totalDiscount
    }

    //在Gird View選擇商品數量
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
            //callback.invoke(0)
        }

        builder.show()
    }

    // 更新 GridView 的外觀(購物車換色)
    private fun updateGridViewAppearance() {
        // 更新商品列表的 GridView
        val adapter = ProductItemAdapter(filteredProductList,shoppingCart)
        binding.grTableData.adapter = adapter
    }

    //查詢特定column並顯示到GridView
    private fun updateGridView(productList: List<ProductItem>, columnName: String?, selectedItem: Any?) {
        //如果 columnName 和 selectedItem 不為空，則篩選商品列表
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
        val adapter = ProductItemAdapter(filteredProductList , shoppingCart)
        binding.grTableData.adapter = adapter
        binding.grTableData.numColumns = 3
    }

    //取得product table
    private fun getProductTable() {
        val databaseHelper = ProductDBHelper(requireContext())  //product Table Helper
        productList = databaseHelper.getAllProducts()   //取得product table items

        databaseHelper.close()
    }

    // 判斷是否是組合商品
    private fun isPairDiscount(productId: String): Boolean {
        val pairDiscountDbHelper = PairDiscountDBHelper(requireContext())
        val db = pairDiscountDbHelper.readableDatabase
        val query = "SELECT * FROM PairDiscountTable WHERE d_pId = $productId"
        val cursor = db.rawQuery(query, null)
        val result = cursor.moveToFirst()
        cursor.close()
        db.close()
        return result
    }

    // 處理組合商品的邏輯
    @SuppressLint("Range")
    private fun handlePairDiscount(pairDiscountId: String) {
        val pairDiscountDbHelper = PairDiscountDBHelper(requireContext())
        val db = pairDiscountDbHelper.readableDatabase

        // 根據 pairDiscountId 查詢 PairDiscountTable 取得相關資訊
        val query = "SELECT * FROM PairDiscountTable WHERE d_pId = $pairDiscountId"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                // 取得組合商品的成員和數量
                val itemSet = cursor.getString(cursor.getColumnIndex("itemSet"))
                val number = cursor.getString(cursor.getColumnIndex("number"))

                // 分割成員和數量
                val items = itemSet.split(",")
                val quantities = number.split(",")

                // 逐一處理組合商品中的每個成員
                for (j in items.indices) {
                    val memberName = items[j]
                    val memberQuantity = quantities[j].toInt()

                    Log.d("成員名稱", memberName)

                    // 根據 memberId 取得商品名稱
                    val productName = getProductDisplayName(memberName)

                    // 在這裡進行相應的處理，例如將成員和數量加入購物車 (這邊暫時先不處理)
                    // shoppingCart.addProduct(memberName, memberQuantity)

                    // 設定成員的折扣金額為 0
                    val memberDiscountInfo = DiscountInfo(
                        discountDescription = "組合商品: $productName",
                        productId = memberName,
                        selectedQuantity = memberQuantity,
                        totalDiscount = 0
                    )
                    discountInfoList.add(memberDiscountInfo)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }

    // 根據 pId 取得商品名稱
    private fun getProductDisplayName(pId: String): String {
        val dbHelper = ProductDBHelper(requireContext())
        val product = dbHelper.getProductsByCondition("pId", pId.toString()).firstOrNull()
        return product?.pName ?: "Unknown Product"
    }

    //確認商品折扣，並顯示在listView
    private fun checkDiscount(selectedProducts: MutableList<ProductItem>): List<DiscountedProduct> {
        val discountItemsInCart: List<DiscountedProduct>
        val databaseHelper = DiscountDBHelper(requireContext())  //Discount Table Helper

        val selectedProductIds = selectedProducts.map { it.pId.toString() }

        // 調用 DiscountDBHelper 中的方法
        discountItemsInCart = databaseHelper.searchDiscountItems("d_pId", selectedProductIds,selectedProducts)
        databaseHelper.close()

        return discountItemsInCart
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}