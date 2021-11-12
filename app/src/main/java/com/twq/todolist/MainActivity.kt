package com.twq.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import com.twq.todolist.Model.Tasks
import com.twq.todolist.Model.todoAdapter
import java.util.*
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var  db: FirebaseFirestore
    private lateinit var dialogView: View
    private lateinit var customDialog: AlertDialog
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var todoAdapter: todoAdapter

    var taskList= mutableListOf<Tasks>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        // Toolbar //
        var mtoolbar = findViewById<Toolbar>(R.id.mToolbar)
        mtoolbar.title = getString(R.string.appName)
        //mtoolbar.setNavigationIcon(R.drawable.ic_edit)
        setSupportActionBar(mtoolbar)

        var searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                    var newData = taskList.filter {task: Tasks -> task.taskName?.toLowerCase()!!.contains(p0!!)  } as MutableList<Tasks>
                    mRecyclerView.adapter = todoAdapter(newData,db)
                    return true
                }
        })



        dialogView = layoutInflater.inflate(R.layout.custom_layout, null)
        customDialog = AlertDialog.Builder(this).setView(dialogView).create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var fab = findViewById<FloatingActionButton>(R.id.floatingActionButtonAdd)
        fab.setOnClickListener {
            addDialog(dialogView)
            customDialog.show()
        }
    }

    override fun onStart() {
            super.onStart()
            mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
            mRecyclerView.layoutManager = LinearLayoutManager(this)

            db.collection("Tasks")
                .addSnapshotListener { result, error ->
                    taskList.clear()
                    if (result != null) {
                        for (document in result) {
                            taskList.add(Tasks(
                                document.id,
                                document.getString("taskName")!!,
                                document.getDate("date") as Date,
                                document.getString("description")!!,
                                document.getDate("creationDate") as Date,
                                document.getBoolean("checkbox")!!
                            ))
                        }
                    }
                    todoAdapter = todoAdapter(taskList,db)
                    mRecyclerView.adapter = todoAdapter
                    todoAdapter.data.sortBy{
                        it.date
                    }

                }
        }

    fun addDialog(view:View){
        var backButton = dialogView.findViewById<ImageView>(R.id.imageViewBackDetails)
        backButton.setOnClickListener {
            customDialog.dismiss()
        }
        var editTextDate = dialogView.findViewById<EditText>(R.id.editTextDateView)
        ////// Date ///////
        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        var date = Date()
        editTextDate.setOnClickListener {
            var datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    editTextDate.setText("$day/${month+1}/$year")
                    date = Date(year,month,day)

                }, year, month, day)
            datePickerDialog.show()
        }
        //DateTime.parse(date.toDate().toString())
        //DateTime.parser()

        // Add button listener //
        var buttonAddDialog = dialogView.findViewById<Button>(R.id.buttonDeleteItem)
        buttonAddDialog?.setOnClickListener {

            var textEditTaskName = dialogView.findViewById<EditText>(R.id.editTextNameView)
            var textEditDesciption = dialogView.findViewById<EditText>(R.id.editTextDescriptionView)
            var TaskName = textEditTaskName?.text.toString()
            var TaskDescription = textEditDesciption?.text.toString()

            var creationDate = Date(year,month,day)

            // Adding the Map //
            val task = hashMapOf(
                "taskName" to TaskName,
                "date" to date,
                "description" to TaskDescription,
                "creationDate" to creationDate,
                "checkbox" to false
            )

            //val taskInstance = Tasks(null, TaskName, date, TaskDescription, creationDate, false)
            db.collection("Tasks").add(task).addOnSuccessListener { dr->
                textEditTaskName?.text?.clear()
                editTextDate?.text?.clear()
                textEditDesciption?.text?.clear()
                //taskList.add(taskInstance)
                //todoAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Task has been added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {  e ->
                Toast.makeText(this, "Not added "+e, Toast.LENGTH_SHORT).show()
            }
            onStart()
            customDialog.dismiss()
        }

    }

    // Tool bar menu items
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_layout,menu)
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.item_search ->{
//                val sendIntent: Intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
//                    type = "text/plain"
//                }
//                val shareIntent = Intent.createChooser(sendIntent, null)
//                startActivity(shareIntent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
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

