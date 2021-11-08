package com.twq.todolist.Model

import java.util.*

data class Tasks(var id: Int? = null,
                 var taskName: String,
                 var date: String,
                 var description: String,
                 var isChecked: Boolean = false
)
