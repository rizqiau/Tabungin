package com.example.ones.data.remote.request

data class AddGoalsRequest(
    val title: String,
    val targetAmount: Long,
    val deadline: String
)