package com.example.ones.data.remote.request

data class UpdateGoalRequest (
    val title: String,
    val targetAmount: Long,
    val deadline: String,
)