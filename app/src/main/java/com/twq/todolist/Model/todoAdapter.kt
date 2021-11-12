package com.twq.todolist.Model

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
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
            holder.tvCbDone!!.isChecked = data[position].checkbox

            /// Calculating due date ///
            var c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            var today = Date(year,month,day)
            var months = data[position].date.month+1


            if (holder.tvCbDone.isChecked){
                holder.tvTaskDate.text = "Completed"
            }else {
                /// Past due ///
                if (today > data[position].date) {
                    holder.tvTaskDate.text = ("Past due: " + data[position].date.date.toString()
                            + "/" + months.toString() + "/" + data[position].date.year.toString())
                    holder.tvTaskDate.setTextColor(Color.parseColor("#FF0000"))
                    holder.tvTaskDate.setTypeface(null, Typeface.ITALIC)
                    /// Due today ///
                } else if (today.equals(data[position].date)) {
                    holder.tvTaskDate.text = ("Due today: " + data[position].date.date.toString()
                            + "/" + months.toString() + "/" + data[position].date.year.toString())
                    holder.tvTaskDate.setTextColor(Color.parseColor("#009CFF"))
                    holder.tvTaskDate.setTypeface(null, Typeface.ITALIC)
                    /// Due date ///
                } else {
                    holder.tvTaskDate.text = ("Due date: " + data[position].date.date.toString()
                            + "/" + months.toString() + "/" + data[position].date.year.toString())
                }
            }
            /// Checkbox ///

            toggleStrickThrough(holder.tvTaskName,data[position].checkbox)
            holder.tvCbDone.setOnCheckedChangeListener { compoundButton, isChecked ->
                if(compoundButton.isChecked){
                    firebaseDB?.collection("Tasks")
                        ?.document(data[position].id!!)
                        ?.update(mapOf(
                            "checkbox" to true
                        ))
                    holder.tvTaskName.paintFlags =holder.tvTaskName.paintFlags or STRIKE_THRU_TEXT_FLAG

                }else{
                    firebaseDB?.collection("Tasks")
                        ?.document(data[position].id!!)
                        ?.update(mapOf(
                            "checkbox" to false
                        ))
                    holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                }
            }




            /// Intent ///
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