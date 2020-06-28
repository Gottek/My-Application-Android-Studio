package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home_page.*

class HomePage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        auth = FirebaseAuth.getInstance()

        pasEncoreInscrit.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
        btnValider.setOnClickListener {
            doLogin()
        }
    }

    public override fun onStart() { // permet de reconnecter l'utilisateur une fois qu'il a quitter l'application
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun doLogin() {
        if (emailText.text.toString().isEmpty()) {
            emailText.error = "please enter an email"
            emailText.requestFocus()
            return
        }
        if (passText.text.toString().isEmpty()) {
            passText.error = "please enter an email"
            passText.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(emailText.text.toString(), passText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext, "You failed to login.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }


    }
}
