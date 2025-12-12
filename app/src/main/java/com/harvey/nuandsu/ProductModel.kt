package com.harvey.nuandsu

import java.io.Serializable


data class Product(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val status: String? = null,
    val imageUri: String? = null,
    val typ: String,
    val pc: Int = 0,
    val des: String,
    val date: String = "",

) : Serializable
