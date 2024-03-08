package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.ZoneOffset

@TypeConverters(Converters::class)

@Entity(tableName = "Invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val paymentIds: String,
    val itemList: String,
    val totalPrice: Int,
    val discount: Int,
    val purchaseTime: Int? = null
)

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
    }
}