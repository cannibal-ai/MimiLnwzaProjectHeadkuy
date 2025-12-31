package com.harvey.nuandsu

import java.io.Serializable


data class Product(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val status: String? = null,
    val imageUri: String? = null,
    val typ: String,
    val pc: Int,
    val des: String,
    val totalCost: Int,
    val date: String = "",
    val expiryDate: String? = null,
    val lastUpdateDate: String? = null

) : Serializable
