package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.Database
import androidx.room.RoomDatabase

//發票資料庫
@Database(entities = [Invoice::class], version = 1)
abstract class InvoiceDataBase : RoomDatabase(){
    abstract fun invoiceDao(): InvoiceDao
}