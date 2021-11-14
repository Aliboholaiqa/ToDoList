package com.twq.todolist


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.twq.todolist.Model.Tasks
import java.util.*

class Activity_list_Item : AppCompatActivity() {
    private val TAG = "DocSnippets"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list__item)

        var db = FirebaseFirestore.getInstance()

        var backButton = findViewById<ImageView>(R.id.imageViewBackDetails)
        backButton.setOnClickListener {
            finish()
        }

        var taskName = findViewById<EditText>(R.id.editTextNameView)
        var taskDate = findViewById<EditText>(R.id.editTextDateView)
        var taskDescription = findViewById<EditText>(R.id.editTextDescriptionView)

        var task = intent.getSerializableExtra("task") as Tasks



        taskName.setText(task.taskName)
        taskDescription.setText(task.description)

        var months = task.date.month+1
        taskDate.setText(task.date.date.toString()+ "/"+ months.toString()+ "/"+task.date.year.toString())

        //--------- Delete ---------//

        var buttonDelete = findViewById<Button>(R.id.buttonDeleteItem)
        buttonDelete?.setOnClickListener {
            var dialogDeleteView = layoutInflater.inflate(R.layout.custom_layout_delete,null)
            var deleteDialog = AlertDialog.Builder(this).setView(dialogDeleteView).create()
            deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            deleteDialog.show()
            var buttonDeleteItem = dialogDeleteView.findViewById<Button>(R.id.buttonDeleteItem)

            buttonDeleteItem.setOnClickListener {
                db.collection("Tasks").document(task.id!!)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                        Log.d(TAG,"Successfully deleted ${task.id!!}") }
                    .addOnFailureListener { Log.d(TAG,"Failed to delete ") }
                deleteDialog.dismiss()
                finish()
            }
            var buttonCancelItem = dialogDeleteView.findViewById<Button>(R.id.buttonCancelItem)
            buttonCancelItem.setOnClickListener {
                deleteDialog.dismiss()
            }
        }


        //--------- Update ---------//

        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        var date = Date(task.date.year,task.date.month,task.date.date)

        taskDate.setOnClickListener {
            var datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    taskDate.setText("$day/${month+1}/$year")
                    date = Date(year,month,day)

                }, year, month, day)
            datePickerDialog.show()
        }

        var buttonUpdate = findViewById<Button>(R.id.buttonUpdateItem)
        buttonUpdate?.setOnClickListener {
            val updateName = taskName?.text.toString()
            val updateDescription = taskDescription?.text.toString()
            var creationDate = Date(year,month,day)
            db.collection("Tasks").document(task.id!!)
                .update(mapOf(
                    "checkbox" to false,
                    "taskName" to updateName,
                    "description" to updateDescription,
                    "date" to date,
                    "creationDate" to creationDate
                ))
                .addOnSuccessListener {
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
                    Log.d(TAG,"Task Updated ${task.id!!}") }
                .addOnFailureListener { Log.d(TAG,"Failed to delete Task ") }
            finish()
        }



    }
}