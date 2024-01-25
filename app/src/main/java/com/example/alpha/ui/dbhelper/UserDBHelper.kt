package com.example.alpha.ui.dbhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

//這邊的User是指用戶，也就是店員
class UserDBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "UserDB.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 在這裡建立資料庫表格等相關邏輯
        db.execSQL("CREATE TABLE IF NOT EXISTS UserTable (" //建立UserTable
                + "uId TEXT PRIMARY KEY,"  //用戶id
                + "uName TEXT,"            //用戶名稱
                + "account TEXT,"          //帳號
                + "password TEXT);")       //密碼

        Log.d("進入table", "link start")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS UserTable;")
        onCreate(db)
    }
}