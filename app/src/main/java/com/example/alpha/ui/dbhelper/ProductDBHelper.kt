package com.example.alpha.ui.dbhelper

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.alpha.R
import com.example.alpha.ui.myObject.DiscountInfo
import com.example.alpha.ui.myObject.ProductItem

//這邊的ProductTable是指商品
class ProductDBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "ProductDB.db"
        private const val DATABASE_VERSION = 1
    }
    override fun onCreate(db: SQLiteDatabase) {
        // 在這裡建立資料庫表格等相關邏輯
        db.execSQL("CREATE TABLE IF NOT EXISTS ProductTable (" //建立ProductTable
                + "pId TEXT PRIMARY KEY,"  //商品id
                + "pName TEXT,"               //商品名稱
                + "pType TEXT,"               //商品分類
                + "pBarcode TEXT,"            //商品條碼
                + "pPrice INTEGER,"           //商品價錢
                + "pNumber INTEGER,"          //商品數量
                + "pPhoto INTEGER);")         //商品圖片
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ProductTable;")
        onCreate(db)
    }

    // 新增一個方法來從資料庫中讀取全部資料
    @SuppressLint("Range")
    fun getAllProducts(): List<ProductItem> {
        val productList = mutableListOf<ProductItem>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ProductTable", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("pId"))
                val imageResId = if (cursor.getInt(cursor.getColumnIndex("pPhoto")) == 0) {
                    R.drawable.ic_hello // 預設照片位置
                } else {
                    cursor.getInt(cursor.getColumnIndex("pPhoto"))
                }
                val name = cursor.getString(cursor.getColumnIndex("pName"))
                val category = cursor.getString(cursor.getColumnIndex("pType"))
                val pBarcode = cursor.getString(cursor.getColumnIndex("pBarcode"))
                val price = cursor.getInt(cursor.getColumnIndex("pPrice"))
                val quantity = cursor.getInt(cursor.getColumnIndex("pNumber"))

                val productItem = ProductItem(id, imageResId, name, category,pBarcode, price, quantity)
                productList.add(productItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return productList
    }

    // 根據商品查詢
    @SuppressLint("Range")
    fun getProductsByCondition(columnName: String, columnValue: String): List<ProductItem> {
        val productList = mutableListOf<ProductItem>()
        val db = readableDatabase

        val query = "SELECT * FROM ProductTable WHERE $columnName = ?"
        val cursor = db.rawQuery(query, arrayOf(columnValue))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("pId"))
                val imageResId = if (cursor.getInt(cursor.getColumnIndex("pPhoto")) == 0) {
                    R.drawable.ic_hello // 預設照片位置
                } else {
                    cursor.getInt(cursor.getColumnIndex("pPhoto"))
                }
                val name = cursor.getString(cursor.getColumnIndex("pName"))
                val category = cursor.getString(cursor.getColumnIndex("pType"))
                val pBarcode = cursor.getString(cursor.getColumnIndex("pBarcode"))
                val price = cursor.getInt(cursor.getColumnIndex("pPrice"))
                val quantity = cursor.getInt(cursor.getColumnIndex("pNumber"))

                val productItem = ProductItem(id, imageResId, name, category, pBarcode, price, quantity)
                productList.add(productItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return productList
    }

    //依據商品id,商品名、商品貨號搜尋
    @SuppressLint("Range")
    fun getProductsByKeyword(keyword: String): List<ProductItem> {
        val productList = mutableListOf<ProductItem>()
        val db = readableDatabase

        val query =
            "SELECT * FROM ProductTable WHERE pName LIKE '%$keyword%' OR pId = '$keyword' OR pBarcode LIKE '%$keyword%'"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("pId"))
                val imageResId = if (cursor.getInt(cursor.getColumnIndex("pPhoto")) == 0) {
                    R.drawable.ic_hello // 預設照片位置
                } else {
                    cursor.getInt(cursor.getColumnIndex("pPhoto"))
                }
                val name = cursor.getString(cursor.getColumnIndex("pName"))
                val category = cursor.getString(cursor.getColumnIndex("pType"))
                val pBarcode = cursor.getString(cursor.getColumnIndex("pBarcode"))
                val price = cursor.getInt(cursor.getColumnIndex("pPrice"))
                val quantity = cursor.getInt(cursor.getColumnIndex("pNumber"))

                val productItem = ProductItem(id, imageResId, name, category, pBarcode, price, quantity)
                productList.add(productItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return productList
    }
}