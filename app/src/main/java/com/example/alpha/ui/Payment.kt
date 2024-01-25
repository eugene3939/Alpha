package com.example.alpha.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.alpha.MainActivity
import com.example.alpha.R
import com.example.alpha.databinding.ActivityMainBinding
import com.example.alpha.databinding.ActivityPaymentBinding
import com.example.alpha.databinding.FragmentHomeBinding
import com.example.alpha.ui.home.HomeFragment
import com.example.alpha.ui.home.HomeViewModel
import com.example.alpha.ui.myObject.DiscountInfo
import com.example.alpha.ui.myObject.ShopCart

class Payment : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

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
            Log.e("PaymentActivity", "Failed to retrieve shoppingCart or discountInfoList from intent.")
        }

        //返回前一頁
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}