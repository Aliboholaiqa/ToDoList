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
        taskDate.setText(task.date.toString())


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
                    .addOnSuccessListener { Log.d(TAG,"Successfully deleted ${task.id!!}") }
                    .addOnFailureListener { Log.d(TAG,"Failed to delete ") }
                deleteDialog.dismiss()
                finish()
            }
            var buttonCancelItem = dialogDeleteView.findViewById<Button>(R.id.buttonCancelItem)
            buttonCancelItem.setOnClickListener {
                deleteDialog.dismiss()
            }
        }



        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        var date = Date()
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

            db.collection("Tasks").document(task.id!!)
                .update(mapOf(
                    "isChecked" to false,
                    "taskName" to updateName,
                    "description" to updateDescription,
                    "date" to date,
                ))
                .addOnSuccessListener { Log.d(TAG,"Task Updated ${task.id!!}") }
                .addOnFailureListener { Log.d(TAG,"Failed to delete Task ") }
            finish()
        }



    }
}