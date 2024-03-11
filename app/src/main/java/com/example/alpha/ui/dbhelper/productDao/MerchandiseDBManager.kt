package com.example.alpha.ui.dbhelper.productDao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class MerchandiseDBManager(context: Context){
    private val db: MerchandiseDataBase = Room.databaseBuilder(
        context,
        MerchandiseDataBase::class.java, "merchandise-database"
    ).build()

    private val merchandiseDao: MerchandiseDao = db.merchandiseDao()

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
}