package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_page.*
import kotlinx.android.synthetic.main.content_main.*

class ProfilePage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()

        val user=intent.getParcelableExtra<Users>("InformationProfile")

        textViewPhone.text="phone number : ${user.phone}"
        textViewFirst.text="First name : ${user.name}"
        textViewLast.text="Last name : ${user.last}"
        textViewMail.text="E-mail : ${auth.currentUser?.email}"
        db.collection("Projects").whereEqualTo("mailUser", auth.currentUser?.email).get()
            .addOnSuccessListener { documents ->
                val documentSize=documents.size()
                textView14.text="nombre de projet(s) : $documentSize"
            }
            .addOnFailureListener { exception ->
                Log.w("ProfilePage", "Error getting documents: ", exception)
            }

    }
}
