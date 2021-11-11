package com.twq.todolist.Model

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*

data class Tasks(
    var id: String? = null,
    var taskName: String?,
    var date: Date,
    var description: String?,
    var creationDate: Date,
    var checkbox: Boolean = false
) : Serializable
