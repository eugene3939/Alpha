package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.alpha.ui.myObject.Invoice

@Database(entities = [Invoice::class], version = 1)
@TypeConverters(Converters::class)
abstract class InvoiceDataBase :RoomDatabase(){
    abstract fun invoiceDao(): InvoiceDao
}

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        return data.split(",")
    }
}