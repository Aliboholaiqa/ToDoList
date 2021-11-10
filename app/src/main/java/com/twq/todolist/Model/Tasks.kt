package com.twq.todolist.Model

import java.io.Serializable
import java.util.*

data class Tasks(
    var id: String? = null,
    var taskName: String?,
    var date: Date,
    var description: String?,
    var isChecked: Boolean = false
) : Serializable
