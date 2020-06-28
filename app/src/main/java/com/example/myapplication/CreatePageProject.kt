package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_page_project.*

class CreatePageProject : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_page_project)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        btn_creat_project.setOnClickListener {

            if (TextUtils.isEmpty(Name_project_editText.text) || TextUtils.isEmpty(editTextDescription.text)
            ){Toast.makeText(this, "inputs can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                var project = Projects(
                    Name_project_editText.text.toString(),
                    true,
                    editTextDescription.text.toString(),
                    auth.currentUser?.email
                )
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("NewProject", project)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
