package com.twq.todolist

import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.params.ColorSpaceTransform
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Timestamp
import com.google.protobuf.TimestampOrBuilder
import com.twq.todolist.Model.Tasks
import com.twq.todolist.Model.todoAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var  db: FirebaseFirestore
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         db =FirebaseFirestore.getInstance()

        // Toolbar //
        var mtoolbar = findViewById<Toolbar>(R.id.mToolbar)
        mtoolbar.title = getString(R.string.appName)
        //mtoolbar.setNavigationIcon(R.drawable.ic_edit)
        setSupportActionBar(mtoolbar)

        // Floating button to add a task to the list
        var fab = findViewById<FloatingActionButton>(R.id.floatingActionButtonAdd)
        var mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)


        db.collection("Tasks").get()
            .addOnSuccessListener { result ->
                var taskList = mutableListOf<Tasks>()
                for (document in result) {
                    //println("${document.getString("member")} ${document.getString("taskName")}")
                    taskList.add(Tasks(null,
                        document.getString("taskName")?:"",
                        (document.get("date")as? Date).toString(),
                        document.getString(" ")?:""))
                }
                println(taskList.size)
                mRecyclerView.adapter = todoAdapter(taskList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }


        dialogView = layoutInflater.inflate(R.layout.custom_layout,null)
        var customDialog = AlertDialog.Builder(this).setView(dialogView) .create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fab.setOnClickListener {
            addDialog(dialogView)
            customDialog.show()


        }
    }


    fun addDialog(view:View){
        // Date and Time //
        var textViewDate = dialogView.findViewById<TextView>(R.id.textViewDate)
        var textViewTime = dialogView.findViewById<TextView>(R.id.textViewTime)
        var editTextDate = dialogView.findViewById<EditText>(R.id.editTextDate)
        var editTextTime = dialogView.findViewById<EditText>(R.id.editTextTime)

        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        editTextDate.setOnClickListener {
            var datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    textViewDate?.text = "$day/${month+1}/$year"
                }, year, month, day)
            datePickerDialog.show()
        }

        var t = Calendar.getInstance()
        var hour = t.get(Calendar.HOUR)
        var minute = t.get(Calendar.MINUTE)

        editTextTime.setOnClickListener {
            var timeDialogListener = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                    textViewTime?.text = "$hour:$minute"
                }, hour, minute, true)
            timeDialogListener.show()
        }
        ///////////////

        // Add button //
        var buttonAddDialog = dialogView.findViewById<Button>(R.id.buttonAddDialog)
        buttonAddDialog?.setOnClickListener {
            //Add dialog
            var textEditTaskName = dialogView.findViewById<EditText>(R.id.inputEditTextTaskName)
            var textEditDesciption = dialogView.findViewById<EditText>(R.id.inputEditTextDescription)
            var TaskName = textEditTaskName?.text.toString()
            var TaskDescription = textEditDesciption?.text.toString()
            var Date = editTextDate?.text.toString()

            val task = hashMapOf(
                "taskName" to TaskName,
                "date" to com.google.firebase.Timestamp(System.currentTimeMillis()),
                "description" to TaskDescription,
                "isChecked" to false
            )
            db.collection("Tasks").add(task).addOnSuccessListener { dr->
                textEditTaskName?.text?.clear()
                editTextDate?.text?.clear()
                Toast.makeText(this, "Task has been added $dr", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {  e ->
                Toast.makeText(this, "Not added "+e, Toast.LENGTH_SHORT).show()
            }
        }
    }









    // Tool bar menu items
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_layout,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_search ->{
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
/*
// Page Transformer for Animation
private const val MIN_SCALE = 0.75f
class DepthPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationZ = -1f

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
   }
 */

