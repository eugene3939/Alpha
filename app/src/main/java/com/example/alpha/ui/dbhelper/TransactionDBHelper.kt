package com.example.alpha.ui.dbhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//這邊的TransactionTable是指交易，也就是發票
class TransactionDBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "TransactionDB.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS TransactionTable (" //建立ProductTable
                + "tId INTEGER PRIMARY KEY AUTOINCREMENT,"  //交易id
                + "tDate DATE,"     //交易日期
                + "tDescription TEXT);")  //交易描述
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS TransactionTable;")
        onCreate(db)
    }
}