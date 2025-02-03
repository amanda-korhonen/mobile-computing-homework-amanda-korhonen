package com.example.composetutorial.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "user_database")
data class User (
    @PrimaryKey (autoGenerate = true) val uid: Int= 0,
    @ColumnInfo(name = "user_name") var userName: String = "User"
    //Sets the default name for the user to be User
)