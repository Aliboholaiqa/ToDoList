package com.twq.todolist.Model

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.twq.todolist.Activity_list_Item
import com.twq.todolist.R
import java.util.*

class todoAdapter(var data: MutableList<Tasks>, var firebaseDB: FirebaseFirestore?): RecyclerView.Adapter<TaskHolder>() {
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
            holder.tvTaskDate.text = data[position].date.toString()
            holder.tvCbDone.isChecked = data[position].isChecked

            toggleStrickThrough(holder.tvTaskName,data[position].isChecked)
            holder.tvCbDone.setOnCheckedChangeListener { compoundButton, isChecked ->
                toggleStrickThrough(holder.tvTaskName,isChecked)
                data[position].isChecked = !data[position].isChecked
            }



            holder.itemView.setOnClickListener {
                var intent = Intent(holder.itemView.context, Activity_list_Item::class.java)
                intent.putExtra("task",data[position])
                holder.itemView.context.startActivity(intent)
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