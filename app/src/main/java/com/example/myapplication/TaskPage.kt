package com.example.myapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_page_project.*
import kotlinx.android.synthetic.main.activity_create_page_section.*
import java.util.*


class TaskPage : AppCompatActivity(), View.OnClickListener {

    lateinit var tasks: MutableList<Tasks>
    lateinit var adapter: TasksAdapter
    var idSection: Int = 0
    private val db = Firebase.firestore
    private val TAG = "TASK SECTION"
    private var ShlagueCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_page_section)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val idProjects = intent.extras?.getString("idProject")
        idSection = intent.extras?.getInt("idSection")!!
        val SectionPosition = intent.extras?.getInt("SectionPosition")
        editTextTitleSection.setText(intent.extras?.getString("title"))
        editTextDescriptionSection.setText(intent.extras?.getString("description"))
        if (idSection == 0) ShlagueCode = UUID.randomUUID().hashCode() else ShlagueCode = idSection

        tasks = mutableListOf<Tasks>()
        reaffichage()
        adapter = TasksAdapter(tasks, this)
        val recyclerView = findViewById(R.id.CheckBox_recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btn_add_task.setOnClickListener {
            if (text_task_add.text.toString() != null) {
                creatNewTask(text_task_add.text.toString())
                text_task_add.setText("")

            }
        }
        btnCreateSection.setOnClickListener {

            if (TextUtils.isEmpty(editTextTitleSection.text) || TextUtils.isEmpty(
                    editTextDescriptionSection.text
                )
            ) {
                Toast.makeText(this, "inputs can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                var section = Sections(
                    ShlagueCode,
                    editTextTitleSection.text.toString(),
                    true,
                    editTextDescriptionSection.text.toString(),
                    idProjects!!
                )
                val intent = Intent(this, SectionPage::class.java)
                intent.putExtra("NewSection", section)
                intent.putExtra("SectionPosition", SectionPosition)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onClick(v: View?) {
            v as CheckBox
            db.collection("Tasks").document(v.id.toString()).update("done", v.isChecked)
                .addOnSuccessListener {Log.i("coucou", "ouiiiiii")}
    }


    private fun creatNewTask(text: String) {
        val task = Tasks(text, false, ShlagueCode)
        tasks.add(task)
        db.collection("Tasks")
            .document(task.idTask.toString())
            .set(task)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Project ${task.text} add successfully")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

    }

    private fun reaffichage() {
        db.collection("Tasks").whereEqualTo("idSection", idSection).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var task = document.toObject(Tasks::class.java)
                    tasks.add(task)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }



}





