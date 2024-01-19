package com.example.alpha.ui.dbhelper

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.alpha.R
import com.example.alpha.ui.myObject.DiscountedProduct
import com.example.alpha.ui.myObject.ProductItem

//這邊的DiscountTable是指商品折扣

class DiscountDBHelper(context: Context): SQLiteOpenHelper(context,
    DiscountDBHelper.DATABASE_NAME,
    null, DiscountDBHelper.DATABASE_VERSION){

    var clickCount: Int = 0 // 新增一個屬性用於保存點擊次數

    companion object {
        private const val DATABASE_NAME = "DiscountTable.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS DiscountTable (" //建立ProductTable
                + "d_pId INTEGER,"          //商品id
                + "d_description TEXT,"      //折扣描述
                + "d_pDiscount DOUBLE,"     //折扣比例 ex:90%
                + "d_Chargebacks INT,"      //折抵額度0表示沒有(不與折扣數量合併)，且優先於折扣比例 ex:折30元
                + "d_pClusterItem TEXT);")  //組合對象(若無則為單項折扣)，沒有填0，若有則不是適用原有折數，以單向扣款為主
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS DiscountTable;")
        onCreate(db)
    }

    // 根據折扣商品查詢(欄位名稱，查詢list,點擊次數)
    @SuppressLint("Range")
    fun searchDiscountItems(columnName: String, columnValues: List<String>, selectedProducts: List<ProductItem>): List<DiscountedProduct> {
        val productList = mutableListOf<DiscountedProduct>()
        val db = readableDatabase

        val placeholders = columnValues.joinToString(",") { "?" }
        val query = "SELECT * FROM DiscountTable WHERE $columnName IN ($placeholders)"
        val cursor = db.rawQuery(query, columnValues.toTypedArray())

        if (cursor.moveToFirst()) {
            do {
                // 使用 DiscountedProduct 的建構子來建立物件
                val discountedProduct = DiscountedProduct(
                    pId = cursor.getInt(cursor.getColumnIndex("d_pId")),
                    pDescription = cursor.getString(cursor.getColumnIndex(("d_description"))),
                    pDiscount = cursor.getDouble(cursor.getColumnIndex("d_pDiscount")),
                    pChargebacks = cursor.getInt(cursor.getColumnIndex("d_Chargebacks")),
                    pClusterItem = cursor.getString(cursor.getColumnIndex("d_pClusterItem")),
                    selectedQuantity = getSelectedQuantity(selectedProducts, cursor.getInt(cursor.getColumnIndex("d_pId")))
                )

                productList.add(discountedProduct)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return productList
    }

    // 輔助函數，從購物車中獲取特定商品的選擇數量
    private fun getSelectedQuantity(selectedProducts: List<ProductItem>, productId: Int): Int {
        val product = selectedProducts.find { it.pId == productId }
        return product?.selectedQuantity ?: 0
    }
}