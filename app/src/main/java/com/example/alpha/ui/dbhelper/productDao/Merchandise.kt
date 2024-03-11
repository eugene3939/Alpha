package com.example.alpha.ui.dbhelper.productDao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Merchandises")

//所有商品
data class Merchandise(
    @PrimaryKey(autoGenerate = true)
    val pId: Int = 0,                //商品編號
    val imageUrl: Int,               //商品圖片url
    val pName: String,               //商品名稱
    val pType: String,               //商品類別
    val pBarcode: String,            //商品條碼
    val pNumber: Int,                //商品數量
    val pPrice: Int,                 //商品價格
    var selectedQuantity: Int = 0,   //購物車選擇數量
):Serializable