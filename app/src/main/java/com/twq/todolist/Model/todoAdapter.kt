package com.twq.todolist.Model

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twq.todolist.R
import java.util.*

class todoAdapter(var data: List<Tasks>): RecyclerView.Adapter<TaskHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            var v = LayoutInflater.from(parent.context).inflate(R.layout.list_row,parent,false)
            return TaskHolder(v)
        }

        private fun toggleStrickThrough(tvTaskName: TextView, isChecked : Boolean){
            if(isChecked){
                tvTaskName.paintFlags =tvTaskName.paintFlags or STRIKE_THRU_TEXT_FLAG
            }else{
                tvTaskName.paintFlags = tvTaskName.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            holder.tvTaskName.text = data[position].taskName
            holder.tvTaskDate.text = data[position].date
            holder.tvCbDone.isChecked = data[position].isChecked
            toggleStrickThrough(holder.tvTaskName,data[position].isChecked)
            holder.tvCbDone.setOnCheckedChangeListener { compoundButton, isChecked ->
                toggleStrickThrough(holder.tvTaskName,isChecked)
                data[position].isChecked = !data[position].isChecked
            }

        }


        override fun getItemCount(): Int {
            return data.size
        }

    }
    class TaskHolder(v: View): RecyclerView.ViewHolder(v){

        var tvTaskName = v.findViewById<TextView>(R.id.textViewTaskName)
        var tvTaskDate = v.findViewById<TextView>(R.id.textViewDate)
        var tvCbDone = v.findViewById<CheckBox>(R.id.checkBoxDone)

    }