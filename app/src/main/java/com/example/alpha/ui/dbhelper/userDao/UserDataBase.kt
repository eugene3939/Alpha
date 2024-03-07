package com.example.alpha.ui.dbhelper.userDao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class UserDataBase : RoomDatabase(){
    abstract fun userDao(): UserDao
}