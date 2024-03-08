package com.example.alpha.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.alpha.MainActivity
import com.example.alpha.R
import com.example.alpha.databinding.ActivityPaymentBinding
import com.example.alpha.ui.dbhelper.invoiceDao.Invoice
import com.example.alpha.ui.dbhelper.invoiceDao.InvoiceDBManager
import com.example.alpha.ui.myObject.DiscountInfo
import com.example.alpha.ui.myObject.PaymentMethod
import com.example.alpha.ui.myObject.ShopCart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//付款頁面
class Payment : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    private var originTotalPrice = 0    //購物車取得總價
    private var discount = 0            //購物車取得折扣

    private var totalPayment = 0    //支付總金額

    private var countingState = true //能否繼續收現

    private val paymentList = mutableListOf<PaymentMethod>() //紀錄支付方式

    private lateinit var databaseManager: InvoiceDBManager //用封裝的方式獲取Dao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化資料庫管理器
        databaseManager = InvoiceDBManager(applicationContext)

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

        //確認支付別項目並顯示
        val itemList = resources.getStringArray(R.array.paymentType)  //全部的支付種類

        //點擊現金支付按鈕
        binding.btnCash.setOnClickListener {
            val enterText = binding.edtCash.text.toString()

            val hasCreditCardPayment = paymentList.any { it.paymentType == "信用卡" && it.paymentAmount == 0}  //是否有信用卡資料

            if (!hasCreditCardPayment){  //只有在信用卡資料清除的狀況下才允許操作
                if (enterText == ""){   //沒有輸入，自動填入全部金額
                    val fullPayment = originTotalPrice - discount

                    updatePaymentAmount(paymentList,"現金",fullPayment)  //新增全部金額
                }else if(enterText.toInt()>=0){ //可溢收
                    updatePaymentAmount(paymentList,"現金",enterText.toInt())  //新增輸入金額
                }else{
                    Toast.makeText(this,"請重新輸入",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"請取消清除信用卡支付",Toast.LENGTH_SHORT).show()
            }

            getPaymentResult()  //更新金額面板
        }

        //點擊信用卡支付按鈕
        binding.btnCashCard.setOnClickListener {
            if(countingState){  //信用卡只要新增成功就不允許修改
                val enterText = binding.edtCash.text.toString()

                val otherPayment = paymentList.sumOf { it.paymentAmount } - (paymentList.find { it.paymentType == "信用卡" }?.paymentAmount ?: 0)

                if (enterText == ""){   //沒有輸入，自動填入全部金額
                    val fullPayment = originTotalPrice - discount

                    updatePaymentAmount(paymentList,"信用卡",fullPayment - otherPayment)  //新增全部金額
                    countingState = false
                }else if(enterText.toInt()>=0 && enterText.toInt()<= originTotalPrice - discount - otherPayment){   //不允許溢收
                    updatePaymentAmount(paymentList,"信用卡",enterText.toInt())  //新增輸入金額
                    countingState = false
                }else{
                    Toast.makeText(this,"請重新輸入金額",Toast.LENGTH_SHORT).show()
                }
                getPaymentResult()  //更新金額面板
            }else{
                Toast.makeText(this,"信用卡支付無法變更",Toast.LENGTH_SHORT).show()
            }

        }

        //透過點擊listView更新付款細節
        binding.lsPaymentWay.setOnItemClickListener{_, _, position, _ ->

            //變更為選擇的顯示金額
            val selectedPaymentType = itemList[position]
            val pay = paymentList.find { it.paymentType == selectedPaymentType }
            if (pay != null) {
                binding.edtCash.setText(pay.paymentAmount.toString())
            }
        }

        //送出交易
        binding.btnSend.setOnClickListener {
            //確認格式正確後儲存到Invoice table
            saveToInvoice(paymentList,shoppingCart)

            //交易完成，回到首頁
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this,"交易成功",Toast.LENGTH_SHORT).show()
        }

        //清除金額
        binding.btnClear.setOnClickListener {
            totalPayment = 0
            binding.edtCash.setText("")
        }

        //上一頁(homeFragment)
        binding.btnBack.setOnClickListener {
            Toast.makeText(this,"交易取消",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //確認格式正確後儲存到Invoice table
    private fun saveToInvoice(paymentList: MutableList<PaymentMethod>, shoppingCart: ShopCart?) {
        lifecycleScope.launch(Dispatchers.IO) {


            databaseManager.addInvoice(Invoice( paymentIds = paymentListToString(paymentList),
                                                itemList = shoppingCartToString(shoppingCart),
                                                totalPrice = originTotalPrice-discount,
                                                discount = discount,
                                                purchaseTime = Date()))
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

        totalPayment = paymentList.sumOf { it.paymentAmount }

        //確認是否付款額大於總金額，如果成功就啟動按鈕
        if (totalPayment >= originTotalPrice - discount){
            binding.btnSend.isEnabled = true
        }

        //如果金額大於0顯示金額，否則顯示0
        val change = totalPayment - (originTotalPrice - discount)
        if (change>=0){
            binding.txtChange.text = "找零: $change"
        }else{
            binding.txtChange.text = "找零: $0"
        }

        //顯示選擇的支付方式和付款額
        val displayPaymentList = paymentList.map { "${it.paymentType}: ${it.paymentAmount}元" }
        val customAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayPaymentList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.textSize = 20f // 設置文字大小為 20sp
                return view
            }
        }
        binding.lsPaymentWay.adapter = customAdapter
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
    }

    // 將付款列表轉換為字串
    private fun paymentListToString(paymentList: MutableList<PaymentMethod>): String {
        val paymentStringBuilder = StringBuilder()
        for (paymentMethod in paymentList) {
            paymentStringBuilder.append("${paymentMethod.paymentType}:${paymentMethod.paymentAmount},")
        }
        return paymentStringBuilder.toString()
    }

    // 將購物車轉換為字串
    private fun shoppingCartToString(shoppingCart: ShopCart?): String {
        // 檢查購物車是否為空
        if (shoppingCart == null || shoppingCart.selectedProducts.isEmpty()) {
            return ""
        }

        // 將購物車的商品列表轉換為字串
        val productListString = shoppingCart.selectedProducts.joinToString(separator = ",") { product ->
            "${product.pName}:${product.pPrice}:${product.selectedQuantity}"
        }

        return productListString
    }
}