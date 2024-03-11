package com.example.alpha.ui.dbhelper.productDao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class MerchandiseDBManager(context: Context){
    private val db: MerchandiseDataBase = Room.databaseBuilder(
        context,
        MerchandiseDataBase::class.java, "merchandise-database"
    ).fallbackToDestructiveMigration().build()

    private val merchandiseDao: MerchandiseDao = db.merchandiseDao()

    //-------一般商品-----------
    //新增商品
    fun insert(m: Merchandise) {
        runBlocking {
            merchandiseDao.insert(merchandise = m)
        }
    }

    //刪除商品
    fun delete(id: Int){
        runBlocking {
            merchandiseDao.delete(id)
        }
    }

    //尋找商品id
    fun getMerchandiseByID(id: Int): Merchandise?{
        return runBlocking {
            merchandiseDao.getMerchandiseByID(id)
        }
    }

    //----------以下為折扣商品------------
    //新增商品
    fun insertDiscount(dm: DiscountMerchandise) {
        runBlocking {
            merchandiseDao.insertDiscount(dm)
        }
    }

    //刪除商品
    fun deleteDiscount(id: Int){
        runBlocking {
            merchandiseDao.delete(id)
        }
    }

    //尋找商品id
    fun getDiscountByID(id: Int): DiscountMerchandise?{
        return runBlocking {
            merchandiseDao.getDiscountByID(id)
        }
    }

    //---------以下為配對商品---------
    //新增商品
    fun insertCluster(cm: ClusterMerchandise) {
        runBlocking {
            merchandiseDao.insertCluster(cm)
        }
    }

    //刪除商品
    fun deleteCluster(id: Int){
        runBlocking {
            merchandiseDao.deleteCluster(id)
        }
    }

    //尋找商品id
    fun getClusterByID(id: Int): ClusterMerchandise?{
        return runBlocking {
            merchandiseDao.getClusterByID(id)
        }
    }
}