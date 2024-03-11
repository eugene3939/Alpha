package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Merchandise::class, DiscountMerchandise::class, ClusterMerchandise::class], version = 2, exportSchema = false)

abstract class MerchandiseDataBase: RoomDatabase(){
    abstract fun merchandiseDao(): MerchandiseDao
}