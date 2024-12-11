package com.example.ones.data.remote.response

data class AddGoalsResponse (
    val message: String,
    val id: String,
    val title: String,
    val targetAmount: Long,
    val createdAt: String,
    val deadline: String
)