package com.example.ones.entity
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Saving(
    var id: Int = 0,
    var title: String? = null,
    var description: String? = null,
) : Parcelable