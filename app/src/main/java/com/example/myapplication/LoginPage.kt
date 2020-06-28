package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_page.*


class LoginPage : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val TAG = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        btnEnregistrer.setOnClickListener {
            if(textMotDePasse.length()<=5){
                Toast.makeText(
                    baseContext, "your password must be at least 6 characters long ",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
            creatUser(db)
            creatConnexionUser()
            }
        }
    }

    private fun creatConnexionUser() {
        auth.createUserWithEmailAndPassword(
            textAdressemail.text.toString(),
            textMotDePasse.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, HomePage::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun creatUser(db:FirebaseFirestore){

        val user = Users(
            textNom.text.toString(),
            textPrenom.text.toString(),
            textNumero.text.toString(),
            false,
            true
        )
            // Add a new document with a generated ID
            db.collection("Users")
                .document(textAdressemail.text.toString())
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "User is add")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

    }

}
