package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "ClusterMerchandises")

data class ClusterMerchandise(
    @PrimaryKey(autoGenerate = true)
    val pId: Int = 0,           //折扣編號
    val itemSet: String,        //包含產品{pId:A, pId:B} 表示對應的產品Id
    val number: String,         //包含數量{1,2} 表示需要(1A+2B)
    val total: Int,             //總折價金額(sqlite中不提供array或list的欄位，所以這邊使用TEXT)
)
