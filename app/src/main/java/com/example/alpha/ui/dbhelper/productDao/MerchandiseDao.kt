package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alpha.ui.dbhelper.invoiceDao.Invoice
import java.util.Date

@Dao
interface MerchandiseDao {
    @Query("SELECT * FROM Merchandises")
    fun getAllMerchandiseTable(): MutableList<Merchandise>?

    @Query("SELECT * FROM Merchandises WHERE pId = :id") //尋找符合項目的單一id
    fun getMerchandiseByID(id: Int): Merchandise?

    @Insert
    suspend fun insert(merchandise: Merchandise)

    @Query("DELETE FROM Merchandises WHERE pId = :id")
    suspend fun delete(id: Int)
}