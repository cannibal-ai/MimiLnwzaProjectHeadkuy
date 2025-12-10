package com.harvey.nuandsu

import java.io.Serializable


data class Product(
    val name: String,
    val quantity: Int,
    val status: String? = null,
    val image: Int,
    val typ: String,
    val pc : Int,
    val des : String
) : Serializable
