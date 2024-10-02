package com.example.securenotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = "notes")

data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val noteTitle: String,
    val noteDesc: String
):Serializable
