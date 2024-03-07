package com.example.alpha.ui.dbhelper.userDao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.runBlocking
class UserDBManager(context: Context) {
    private val db: UserDataBase = Room.databaseBuilder(
        context,
        UserDataBase::class.java, "user-database"
    ).build()

    private val userDao: UserDao = db.userDao()

    //新增用戶
    fun addUser(user: User) {
        runBlocking {
            userDao.insert(user)
        }
    }

    //檢查是否有符合id的用戶
    fun getUserById(userIdToInsert: Int): User? {
        return runBlocking {
            userDao.getUserById(userIdToInsert)
        }
    }

    //檢查是否有符合帳號密碼的用戶
    fun loginByAccPas(acc: String, pas: String): User?{
        return runBlocking {
            userDao.loginByAccPas(acc,pas)
        }
    }

}