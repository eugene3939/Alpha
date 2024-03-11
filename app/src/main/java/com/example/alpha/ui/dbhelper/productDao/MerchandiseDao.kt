package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

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

//  折扣商品
    @Insert
    suspend fun insertDiscount(dm: DiscountMerchandise)

    @Query("DELETE FROM DiscountMerchandises WHERE pId = :id")
    suspend fun deleteDiscount(id: Int)

    @Query("SELECT * FROM DiscountMerchandises WHERE pId = :id") //尋找符合項目的單一id
    fun getDiscountByID(id: Int): DiscountMerchandise?

//    組合商品
@Insert
    suspend fun insertCluster(cm: ClusterMerchandise)

    @Query("DELETE FROM ClusterMerchandises WHERE pId = :id")
    suspend fun deleteCluster(id: Int)

    @Query("SELECT * FROM ClusterMerchandises WHERE pId = :id") //尋找符合項目的單一id
    fun getClusterByID(id: Int): ClusterMerchandise?
}