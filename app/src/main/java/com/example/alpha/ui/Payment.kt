package com.example.alpha.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.alpha.MainActivity
import com.example.alpha.databinding.ActivityPaymentBinding
import com.example.alpha.ui.myAdapter.DiscountProductAdapter
import com.example.alpha.ui.myAdapter.ShopCartAdapter
import com.example.alpha.ui.myObject.DiscountInfo
import com.example.alpha.ui.myObject.ShopCart

//付款頁面
class Payment : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    private var originTotalPrice = 0    //購物車取得總價
    private var discount = 0            //購物車取得折扣

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)  // 注意這裡的修改

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

        //顯示折扣和明細內容
        showBuyCartInformation(shoppingCart, discountInfoList)

        //即時確認文本是否大於小記金額(確認付款按鈕會顯示)
        binding.edtCash.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 在文本變化之前執行的操作
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 在文本變化時執行的操作
            }

            // 在文本變化之後執行的操作
            override fun afterTextChanged(s: Editable?) {
                //只有支付金額大於應付金額才能使用(確認付款按鈕)
                val text = binding.edtCash.text.toString()
                if (text.isNotEmpty()) {
                    val value = text.toIntOrNull()
                    binding.btnConfirmPayment.isEnabled = value != null && value >= originTotalPrice-discount  //只有大於總金額-折扣，才顯示按鈕(測試)
                } else {
                    binding.btnConfirmPayment.isEnabled = false
                }
            }
        })

        //清除金額
        binding.btnClear.setOnClickListener {
            binding.edtCash.setText("")
        }

        //上一頁(homeFragment)
        binding.btnBack.setOnClickListener {
            Toast.makeText(this,"交易取消",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //取得折扣額和總價
    @SuppressLint("SetTextI18n")
    private fun getDiscountAndTotalPrice(shoppingCart: ShopCart?, discountInfoList: ArrayList<DiscountInfo>?) {
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

        binding.txtTotalPrice.text = "總價: ${originTotalPrice - discount}"
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