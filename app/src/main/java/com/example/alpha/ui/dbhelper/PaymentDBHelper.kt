import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.alpha.ui.myObject.Transaction

// 定義 SQLite 資料庫助手類別
class PaymentDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PaymentDB"
        private const val TABLE_NAME = "Payments"
        private const val KEY_ID = "id"
        private const val KEY_TYPE = "type"
        private const val KEY_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY, $KEY_TYPE TEXT, $KEY_AMOUNT REAL)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 新增支付
    fun addPayment(payment: Transaction): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TYPE, payment.type)
            put(KEY_AMOUNT, payment.amount)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    // 取得所有支付
    @SuppressLint("Range")
    fun getAllPayments(): ArrayList<Transaction> {
        val paymentList = ArrayList<Transaction>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val type = cursor.getString(cursor.getColumnIndex(KEY_TYPE))
                val amount = cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT))
                val payment = Transaction(id, type, amount)
                paymentList.add(payment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return paymentList
    }
}
