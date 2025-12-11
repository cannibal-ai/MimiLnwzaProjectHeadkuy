package com.harvey.nuandsu

import java.io.Serializable


data class Product(
    val name: String,
    val quantity: Int,
    val status: String? = null,
    val image: Int,
    val typ: String,
    val pc: Int = 0,
    val des : String,
    val date: String = ""
) : Serializable

