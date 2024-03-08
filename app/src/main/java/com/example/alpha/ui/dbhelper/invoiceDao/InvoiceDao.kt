package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.*
import java.util.Date

// 定義發票類別

@Dao
interface InvoiceDao{
    @Insert
    fun addInvoice(invoice: Invoice): Long

    @Query("SELECT * FROM Invoices")
    fun getAllInvoicesTable(): MutableList<Invoice>?

    @Query("SELECT * FROM Invoices WHERE id = :id") //尋找符合項目的單一id
    fun getInvoiceByID(id: Long): Invoice?

    // 添加用於更新登入時間的 Query
    @Query("UPDATE Invoices SET purchaseTime = :purchaseTime WHERE id = :id")
    fun updateUserLoginTime(id: Long, purchaseTime: Date)

    @Insert
    suspend fun insert(invoice: Invoice)

    @Query("DELETE FROM invoices WHERE id = :id")
    suspend fun deleteById(id: Long)
}