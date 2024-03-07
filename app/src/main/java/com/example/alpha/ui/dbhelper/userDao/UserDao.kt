package com.example.alpha.ui.dbhelper.userDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    //檢查是否有符合id的用戶
    @Query("SELECT * FROM Users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    //確認帳號密碼是否正確
    @Query("SELECT * FROM Users WHERE account = :acc AND password = :pas")
    suspend fun loginByAccPas(acc: String,pas: String): User?

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}