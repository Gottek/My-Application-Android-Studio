package com.example.myapplication

import SectionAdapter
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_section_page.*

class SectionPage : AppCompatActivity(), SectionAdapter.sectionItemListener { //View.OnClickListener

    private val db = Firebase.firestore
    private val TAG = "Section Activity"
    lateinit var sections: MutableList<Sections>
    lateinit var adapter: SectionAdapter
    private var idProjects: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section_page)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.sharedPreferences = getSharedPreferences(".SectionPage", 0)
        if (intent.extras?.getString("idProject") != null) {
            idProjects = intent.extras?.getString("idProject")
            sharedPreferences.edit().putString("idProject", idProjects).apply()
        }
        idProjects = sharedPreferences.getString("idProject", "c")
        sections = mutableListOf<Sections>()
        reaffichage()
        adapter = SectionAdapter(sections, this)
        val recyclerView = findViewById(R.id.section_list_recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        section_fab.setOnClickListener {
            creatNewSection()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        } else {
            val section = data.getParcelableExtra<Sections>("NewSection")
            val position = data.getIntExtra("SectionPosition", -1)
            Log.i(TAG, "${section.idSection}")
            if (requestCode == 0) sections.add(section) else  sections[position] = section

            db.collection("Sections")
                .document(section.idSection.toString())
                .set(section)
                .addOnSuccessListener { result ->
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    private fun reaffichage() {
        db.collection("Sections").whereEqualTo("idProjects", idProjects).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val section = document.toObject(Sections::class.java)
                    sections.add(section)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun creatNewSection() {
        val intent2 = Intent(this, TaskPage::class.java)
        intent2.putExtra("idProject", idProjects)
        startActivityForResult(intent2, 0)
    }

    override fun onSectionSelected(section: Sections, position: Int) {
        //Log.i(TAG,"voici ce que t'as selectionné ${section} et voici donc sa position dans la liste ${position}")
        Toast.makeText(this, "voici l'id " + section.idSection, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, TaskPage::class.java)
        intent.putExtra("idProject", sharedPreferences.getString("idProject", "c"))
        intent.putExtra("idSection", section.idSection)
        intent.putExtra("SectionPosition", position)
        intent.putExtra("title", section.title)
        intent.putExtra("description", section.description)
        startActivityForResult(intent, 1)
    }

    override fun onSectionDeleted(section: Sections, position: Int) {
         db.collection("Sections").document(section.idSection.toString())
                .delete()
                .addOnSuccessListener { Log.d("MAIN ACTIVITY", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("MAIN ACTIVITY", "Error deleting document", e) }
        sections.removeAt(position)
        adapter.notifyDataSetChanged()
    }

}


