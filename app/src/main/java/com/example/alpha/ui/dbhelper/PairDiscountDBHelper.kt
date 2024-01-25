package com.example.alpha.ui.dbhelper

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.alpha.R
import com.example.alpha.ui.myObject.ProductItem

//促銷折扣
class PairDiscountDBHelper(context: Context): SQLiteOpenHelper(context,
    PairDiscountDBHelper.DATABASE_NAME,
    null, PairDiscountDBHelper.DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PairDiscountTable.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS PairDiscountTable (" //建立PairDiscountTable
                + "d_pId INTEGER,"     //折扣編號
                + "itemSet TEXT,"      //包含產品{pId:A, pId:B} 表示對應的產品Id
                + "number TEXT,"       //包含數量{1,2} 表示需要(1A+2B)
                + "total INT);")       //總折價金額(sqlite中不提供array或list的欄位，所以這邊使用TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS PairDiscountTable;")
        onCreate(db)
    }
}