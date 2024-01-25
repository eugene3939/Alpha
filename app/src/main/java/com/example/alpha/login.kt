package com.example.alpha

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.alpha.databinding.ActivityLoginBinding
import com.example.alpha.ui.dbhelper.DiscountDBHelper
import com.example.alpha.ui.dbhelper.PairDiscountDBHelper
import com.example.alpha.ui.dbhelper.ProductDBHelper
import com.example.alpha.ui.dbhelper.TransactionDBHelper
import com.example.alpha.ui.dbhelper.UserDBHelper

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbrw: SQLiteDatabase

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 使用 View Binding 初始化綁定
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //整檔
        createTransactionDB() //創建TransactionDB
        createProductDB()     //創建ProductDB
        createDiscountDB()    //創建DiscountDB
        createPairDiscountDB()//創建PairDiscountDB
        createUserDB()        //創建UserDB

        // 登入按鈕
        binding.btnLogin.setOnClickListener {
            // 取得帳號、密碼
            val acc = binding.edtAcc.text.toString()
            val pas = binding.edtPas.text.toString()

            val loginQuery = "SELECT * FROM UserTable WHERE account = '$acc' AND password = '$pas';"
            val loginCursor = dbrw.rawQuery(loginQuery, null)

            // 檢查是否有查詢結果
            if (loginCursor.moveToFirst()) {
                val userName = loginCursor.getString(loginCursor.getColumnIndex("uName")) //取得用戶名稱
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Welcome: $userName", Toast.LENGTH_SHORT).show()
                Log.d("用戶登入成功", "用戶名稱: $userName")
                startActivity(intent)
            } else {
                // 資料庫中未包含 User 的資料
                Toast.makeText(this, "登入失敗", Toast.LENGTH_SHORT).show()
                Log.d("登入失敗提示: ", loginQuery)
            }

            loginCursor?.close() // 確保在使用完畢後關閉 Cursor
        }
    }

    private fun createDatabase(dbHelper: SQLiteOpenHelper, tableName: String, defaultData: List<String>) {
        dbrw = dbHelper.writableDatabase

        // 檢查資料表是否為空
        val isEmptyQuery = "SELECT COUNT(*) FROM $tableName;"
        val countCursor = dbrw.rawQuery(isEmptyQuery, null)

        if (countCursor.moveToFirst()) {
            val count = countCursor.getInt(0)

            // 資料表為空，新增預設資料進table
            if (count == 0) {
                Log.d("$tableName 為空", "還沒有放資料")

                for (data in defaultData) {
                    dbrw.execSQL(data)
                }

                Log.d("成功新增", "${defaultData.size} 組預設資料")
            } else {
                Log.d("$tableName 不為空", "他一共有 $count 組rows.")
            }
        } else {
            Log.e("$tableName 有其他問題", "Error in counting rows.")
        }

        countCursor.close()
    }

    //修改欄位需要修改login(本頁)、dbHelper(對應的),物件(product, discountProduct)
    private fun createUserDB() {
        val dbHelper = UserDBHelper(this)
        val defaultUserData = listOf(
            "INSERT INTO UserTable(uId, uName, account, password) VALUES(1,'Eugene', 1, 1);",
            "INSERT INTO UserTable(uId, uName, account, password) VALUES(2,'Oscar', 3, 3);"
        )
        createDatabase(dbHelper, "UserTable", defaultUserData)
    }

    private fun createProductDB() {
        val dbHelper = ProductDBHelper(this)
        val defaultProductData = listOf(
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(1,'Apple', '水果','SBC', 50, 100, 0);",
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(2,'Pineapple', '水果','123', 100, 80, 0);",
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(3,'Snapple', '其他','A12', 200, 60, 0);",
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(17,'可可亞', '食物','ABC', 500, 80, 0);",
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(18,'西瓜', '飲料','RCT', 230, 60, 0);",
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(19,'綠茶', '飲料','CCC', 80, 25, 0);",
            "INSERT INTO ProductTable(pId,pName, pType,pBarcode, pPrice, pNumber,pPhoto) VALUES(20,'Apple set', '組合商品','RTX', 300, 200, 0);"
        )
        createDatabase(dbHelper, "ProductTable", defaultProductData)
    }

    private fun createDiscountDB() {    //單品折扣
        val dbHelper = DiscountDBHelper(this)
        val defaultDiscount = listOf(
            "INSERT INTO DiscountTable(d_pId, d_description, d_pDiscount, d_Chargebacks) VALUES(1,'蘋果9折', 0.1 , 0);",//即商品編號1號打九折
            "INSERT INTO DiscountTable(d_pId, d_description, d_pDiscount, d_Chargebacks) VALUES(2,'單品折30', 0, 30);",//即單品折讓30元
            "INSERT INTO DiscountTable(d_pId, d_description, d_pDiscount, d_Chargebacks) VALUES(3,'單品折10', 0, 10);",//即單品折讓10元
            "INSERT INTO DiscountTable(d_pId, d_description, d_pDiscount, d_Chargebacks) VALUES(20,'組合商品折60', 0, 60);"//即單品折讓10元 apple+Pineapple+Snapple折50元
        )
        createDatabase(dbHelper, "DiscountTable", defaultDiscount)
    }

    //創建組合商品的Table
    private fun createPairDiscountDB() {//組合商品
        // 使用 PairDiscountDBHelper 插入一個買 id=1,2,3 就折 50 元的物件

        val dbHelper = PairDiscountDBHelper(this)
        val defaultPairDiscountData = listOf(
            "INSERT INTO PairDiscountTable(d_pId, itemSet, number, total) VALUES(20,'1,2,3','1,2,3',60);"
        )
        createDatabase(dbHelper, "PairDiscountTable", defaultPairDiscountData)
    }

    //發票資訊的Table
    private fun createTransactionDB() {
        val dbHelper = TransactionDBHelper(this)
        val defaultTransactionData = listOf(
            "INSERT INTO TransactionTable(tDate, tDescription) VALUES('2018-12-10','0');",
            "INSERT INTO TransactionTable(tDate, tDescription) VALUES('2018-12-11','0');",
            "INSERT INTO TransactionTable(tDate, tDescription) VALUES('2018-12-12','0');"
        )
        createDatabase(dbHelper, "TransactionTable", defaultTransactionData)
    }

    override fun onDestroy() {
        // 在 Activity 銷毀時關閉資料庫連接
        dbrw.close()
        super.onDestroy()
    }
}