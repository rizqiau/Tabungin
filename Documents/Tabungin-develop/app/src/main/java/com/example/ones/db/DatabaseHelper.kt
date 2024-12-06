package com.example.ones.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ones.db.DatabaseContract.SavingColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbsavingapp"
        private const val DATABASE_VERSION = 1
        private val SQL_CREATE_TABLE_SAVING = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.SavingColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.SavingColumns.TITLE} TEXT NOT NULL," +
                " ${DatabaseContract.SavingColumns.DESCRIPTION} TEXT NOT NULL)"

    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_SAVING)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}