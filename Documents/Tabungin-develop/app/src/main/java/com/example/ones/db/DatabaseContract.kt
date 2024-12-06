package com.example.ones.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class SavingColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "saving"
            const val _ID = "_id"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
        }
    }
}