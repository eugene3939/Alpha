package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Date

//發票資料庫
@Database(entities = [Invoice::class], version = 3)
@TypeConverters(InvoiceDataBase.DateConverter::class) // 添加類型轉換器

abstract class InvoiceDataBase : RoomDatabase(){
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Invoices ADD COLUMN purchaseTime DATETIME")
            }
        }
    }

    //日期的類別轉換器
    class DateConverter {
        @TypeConverter
        fun fromDate(date: Date?): Long? {
            return date?.time
        }

        @TypeConverter
        fun toDate(timestamp: Long?): Date? {
            return timestamp?.let { Date(it) }
        }
    }
}