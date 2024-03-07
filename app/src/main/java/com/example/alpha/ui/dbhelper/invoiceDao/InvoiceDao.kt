package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.*

// 定義發票類別

@Dao
interface InvoiceDao{
    @Insert
    fun addInvoice(invoice: Invoice): Long

    @Query("SELECT * FROM Invoices")
    fun getAllInvoicesTable(): List<Invoice>

    @Query("SELECT * FROM Invoices WHERE id = :id")
    fun getInvoiceByID(id: Long): List<Invoice>

    @Insert
    suspend fun insert(invoice: Invoice)

    @Delete
    suspend fun delete(invoice: Invoice)
}