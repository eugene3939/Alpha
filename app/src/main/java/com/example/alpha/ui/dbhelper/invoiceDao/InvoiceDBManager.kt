package com.example.alpha.ui.dbhelper.invoiceDao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import java.util.Date

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
    fun getInvoiceByID(id: Long): Invoice?{
        return runBlocking {
            invoiceDao.getInvoiceByID(id)
        }
    }

    //更新為預設發票日期
    fun updateUserLoginTime(id: Long, purchaseTime: Date){
        runBlocking {
            invoiceDao.updateUserLoginTime(id,purchaseTime)
        }
    }


    //搜尋所有發票
    fun getAllInvoicesTable(): MutableList<Invoice>?{
        return runBlocking {
            invoiceDao.getAllInvoicesTable()
        }
    }
}