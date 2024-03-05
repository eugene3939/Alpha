package com.example.alpha.ui.dbhelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.alpha.ui.dbhelper.invoiceDao.InvoiceDao
import com.example.alpha.ui.myObject.Invoice

class InvoiceDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), InvoiceDao {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "InvoiceDB"
        const val TABLE_NAME = "Invoices"
        const val KEY_ID = "id"
        const val KEY_PAYMENT_IDS = "payment_ids"
        const val KEY_ITEM_LIST = "item_list"
        const val KEY_TOTAL_PRICE = "total_price"
        const val KEY_DISCOUNT = "discount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY, $KEY_PAYMENT_IDS TEXT, $KEY_ITEM_LIST TEXT, $KEY_TOTAL_PRICE REAL, $KEY_DISCOUNT REAL)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 新增發票
    override fun addInvoice(invoice: Invoice): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PAYMENT_IDS, invoice.paymentIds.joinToString(","))
            put(KEY_ITEM_LIST, invoice.itemList.joinToString(","))
            put(KEY_TOTAL_PRICE, invoice.totalPrice)
            put(KEY_DISCOUNT, invoice.discount)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    override fun getAllInvoicesTable(): List<Invoice> {
        val invoiceList = mutableListOf<Invoice>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(KEY_ID))
                val paymentIds = cursor.getString(cursor.getColumnIndex(KEY_PAYMENT_IDS)).split(",")
                val itemList = cursor.getString(cursor.getColumnIndex(KEY_ITEM_LIST)).split(",")
                val totalPrice = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_PRICE))
                val discount = cursor.getInt(cursor.getColumnIndex(KEY_DISCOUNT))
                val invoice = Invoice(id, paymentIds, itemList, totalPrice, discount)
                invoiceList.add(invoice)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return invoiceList
    }

    override fun deleteInvoiceTable(invoiceId: String): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME, "$KEY_ID=?", arrayOf(invoiceId))
        db.close()
        return success
    }
}