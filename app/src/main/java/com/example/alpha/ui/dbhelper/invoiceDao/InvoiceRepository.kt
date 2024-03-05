package com.example.alpha.ui.dbhelper.invoiceDao

import android.content.Context
import com.example.alpha.ui.dbhelper.InvoiceDBHelper
import com.example.alpha.ui.myObject.Invoice

class InvoiceRepository(context: Context) {
    private val helperDAO: InvoiceDao = InvoiceDBHelper(context)

    // 新增發票
    fun addInvoice(invoice: Invoice): Long {
        return helperDAO.addInvoice(invoice)
    }

    // 取得所有發票
    fun getAllInvoices(): List<Invoice> {
        return helperDAO.getAllInvoicesTable()
    }

    // 刪除發票
    fun deleteInvoice(invoiceId: String): Int {
        return helperDAO.deleteInvoiceTable(invoiceId)
    }
}