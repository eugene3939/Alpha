package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Merchandise::class], version = 1)

abstract class MerchandiseDataBase: RoomDatabase(){
    abstract fun merchandiseDao(): MerchandiseDao
}