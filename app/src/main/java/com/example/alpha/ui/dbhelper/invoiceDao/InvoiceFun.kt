package com.example.alpha.ui.dbhelper.invoiceDao

import androidx.room.*
import com.example.alpha.ui.myObject.Invoice
import kotlinx.coroutines.flow.Flow

// 定義發票類別

@Dao
interface InvoiceDao{
    @Insert
    fun addInvoice(invoice: Invoice): Long

    @Query("SELECT * FROM InvoiceTable")
    fun getAllInvoicesTable(): List<Invoice>

    @Query("DELETE FROM InvoiceTable WHERE id = :invoiceId")
    fun deleteInvoiceTable(invoiceId: String): Int
}