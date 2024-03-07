package com.example.alpha.ui.dbhelper.invoiceDao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class InvoiceDBManager(context: Context) {
    private val db: InvoiceDataBase = Room.databaseBuilder(
        context,
        InvoiceDataBase::class.java, "invoice-database"
    ).build()

    private val invoiceDao: InvoiceDao = db.invoiceDao()

    //新增發票
    fun addInvoice(invoice: Invoice) {
        runBlocking {
            invoiceDao.insert(invoice)
        }
    }

    //尋找是否存在發票單號
    fun getInvoiceByID(id: Long){
        runBlocking {
            invoiceDao.getInvoiceByID(id)
        }
    }

    //搜尋所有發票
    fun getAllInvoicesTable(){
        runBlocking {
            invoiceDao.getAllInvoicesTable()
        }
    }
}