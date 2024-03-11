package com.example.alpha.ui.dbhelper.userDao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                    //用戶id
    val name: String,                   //用戶名稱
    val account: String,                //用戶帳號
    val password: String                //用戶密碼
)