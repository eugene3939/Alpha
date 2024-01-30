package com.example.alpha.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.alpha.MainActivity
import com.example.alpha.R
import com.example.alpha.databinding.ActivityPaymentBinding
import com.example.alpha.ui.myAdapter.DiscountProductAdapter
import com.example.alpha.ui.myAdapter.ShopCartAdapter
import com.example.alpha.ui.myObject.DiscountInfo
import com.example.alpha.ui.myObject.PaymentMethod
import com.example.alpha.ui.myObject.ShopCart
import kotlin.reflect.typeOf

//付款頁面
class Payment : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    private var originTotalPrice = 0    //購物車取得總價
    private var discount = 0            //購物車取得折扣

    private var totalPayment = 0    //支付總金額

    private var paymentMethodNow = 0//目前的支付方式

    private val paymentList = mutableListOf<PaymentMethod>() //紀錄支付方式

    //支付類型

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 檢索從 Intent 傳遞的 Serializable 物件
        val shoppingCart = intent.getSerializableExtra("shoppingCart") as? ShopCart
        val discountInfoList = intent.getSerializableExtra("discountInfoList") as? ArrayList<DiscountInfo>

        // 確認 Serializable 物件不為空且符合預期類型
        if (shoppingCart != null && discountInfoList != null) {

            for (i in shoppingCart.selectedProducts){
                Log.d("購物車資訊","$i")
            }

            for (j in discountInfoList){
                Log.d("打折資訊","$j")
            }
            // 在這裡處理 shoppingCart 和 discountInfoList
        } else {
            // 如果無法檢索到或轉換 Serializable 物件，進行錯誤處理
            Log.e("檢索錯誤", "無法從intent取得shoppingCart和discountInfoList物件")
        }

        //取得折扣額和總價
        getDiscountAndTotalPrice(shoppingCart, discountInfoList)
        //顯示找零
        getPaymentResult()
        //顯示折扣和明細內容
        showBuyCartInformation(shoppingCart, discountInfoList)

        //切換支付方式
        binding.spPaymentWay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                paymentMethodNow = position
                Log.d("切換付款方式", paymentMethodNow.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //確認支付別項目並顯示
        val itemList = resources.getStringArray(R.array.paymentType)  //全部的支付種類
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,itemList)
        binding.spPaymentWay.adapter = spinnerAdapter

        //透過點擊listView更新付款細節
        binding.lsPaymentWay.setOnItemClickListener{_, _, position, _ ->
            //Toast.makeText(this,"你點擊了 $position",Toast.LENGTH_SHORT).show()

            //透過顯示文字更新付款內容
            binding.spPaymentWay.setSelection(position)                       //選擇支付方式
            //變更為選擇的顯示金額
            val selectedPaymentType = itemList[position]
            val pay = paymentList.find { it.paymentType == selectedPaymentType }
            if (pay != null) {
                binding.edtCash.setText(pay.paymentAmount.toString())
            }
        }

        //點擊確定後確定支付
        binding.btnConfirm.setOnClickListener {
            val enterText = binding.edtCash.text.toString().toInt()

            if (enterText>=0){
                //更新付款別和金額
                updatePaymentAmount(paymentList, itemList[paymentMethodNow], enterText)
                showBuyCartInformation(shoppingCart, discountInfoList)
            }
        }

        //清除金額
        binding.btnClear.setOnClickListener {
            totalPayment = 0
            binding.edtCash.setText("0")
        }

        //上一頁(homeFragment)
        binding.btnBack.setOnClickListener {
            Toast.makeText(this,"交易取消",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 更新付款方式的付款金額
    private fun updatePaymentAmount(paymentList: MutableList<PaymentMethod>, paymentType: String, amount: Int) {
        val existingPayment = paymentList.find { it.paymentType == paymentType }

        if (existingPayment != null) {
            // 如果已存在相同的付款方式，則更新其付款金額
            existingPayment.paymentAmount = amount
            Log.d("已經存在","更新為$amount")
        }else{
            if (amount != 0) {  //沒有已存在物件的情況下新增物件
                paymentList.add(PaymentMethod(paymentType, amount))
                Log.d("尚未存在","加入$amount")
            }
        }

        if (amount == 0) {
            // 如果金額=0，刪除物件
            // 根據PaymentMethod 類有一個屬性來標識唯一性，比如 paymentType
            val index = paymentList.indexOfFirst { it.paymentType == paymentType }
            if (index != -1) {
                paymentList.removeAt(index)
                Toast.makeText(this, "金額為0，刪除對象", Toast.LENGTH_SHORT).show()
                // 刪除付款方式後，更新 Spinner 和 EditText 元素
                val itemList = resources.getStringArray(R.array.paymentType)
                val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
                binding.spPaymentWay.adapter = spinnerAdapter
                binding.edtCash.setText(totalPayment.toString())
            }
        }

        // 更新付款方式後，刷新畫面以顯示最新的付款方式列表
        getPaymentResult()
    }

    //顯示找零
    @SuppressLint("SetTextI18n")
    private fun getPaymentResult() {
        binding.txtPayment.text = "支付: $totalPayment"
        binding.txtTotalPrice.text = "總價: ${originTotalPrice - discount}"

        //如果金額大於0顯示金額，否則顯示0
        val change = totalPayment - (originTotalPrice - discount)
        if (change>=0){
            binding.txtChange.text = "找零: $change"
        }else{
            binding.txtChange.text = "找零: $0"
        }

        //顯示選擇的支付方式和付款額
        val displayPaymentList = paymentList.map { "${it.paymentType}: ${it.paymentAmount}元" }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayPaymentList)
        binding.lsPaymentWay.adapter = spinnerAdapter
    }

    //取得折扣額和總價
    @SuppressLint("SetTextI18n")
    private fun getDiscountAndTotalPrice(shoppingCart: ShopCart?, discountInfoList: ArrayList<DiscountInfo>?) {
        //找零金額
        var change = 0
        //取得購物車總價
        if (shoppingCart != null) {
            for (i in shoppingCart.selectedProducts){
                originTotalPrice+=i.pPrice*i.selectedQuantity
            }
        }
        //取得折扣總額
        if (discountInfoList != null) {
            for (j in discountInfoList){
                discount+=j.totalDiscount
            }
        }
    }

    //顯示折扣和明細內容
    private fun showBuyCartInformation(
        shoppingCart: ShopCart?,
        discountInfoList: ArrayList<DiscountInfo>?
    ) {
        //顯示購物車內容
        val buyCartAdapter = shoppingCart?.let { ShopCartAdapter(it.selectedProducts) }
        binding.lsBuyCart.adapter = buyCartAdapter
        //顯示明細
        val discountAdapter = discountInfoList?.let { DiscountProductAdapter(it) }
        binding.lsDiscount.adapter = discountAdapter
    }
}