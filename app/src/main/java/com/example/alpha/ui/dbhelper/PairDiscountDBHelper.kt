package com.example.alpha.ui.dbhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
                + "pd_pId INTEGER,"            //促銷編號
                + "pd_Description TEXT,"       //促銷說明
                + "pd_Chargebacks INTEGER,"       //折讓金額
                + "pd_clusterItems TEXT);")     //組合項目(sqlite中不提供array或list的欄位，所以這邊使用TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS PairDiscountTable;")
        onCreate(db)
    }
}