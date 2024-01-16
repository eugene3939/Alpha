package com.example.alpha.ui.dbhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//這邊的ProductTable是指商品
class ProductDBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "ProductDB.db"
        private const val DATABASE_VERSION = 1
    }
    override fun onCreate(db: SQLiteDatabase) {
        // 在這裡建立資料庫表格等相關邏輯
        db.execSQL("CREATE TABLE IF NOT EXISTS ProductTable (" //建立ProductTable
                + "pId INTEGER PRIMARY KEY AUTOINCREMENT,"  //商品id
                + "pName TEXT,"     //商品名稱
                + "pType TEXT,"      //商品分類
                + "pPrice INTEGER," //商品價錢
                + "pNumber INTEGER," //商品數量
                + "pPhoto TEXT);")  //商品圖片

//        Log.d("進入table", "link start")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ProductTable;")
        onCreate(db)
    }

}