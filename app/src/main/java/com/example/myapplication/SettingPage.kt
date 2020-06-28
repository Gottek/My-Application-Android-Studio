package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_setting_page.*
import kotlinx.android.synthetic.main.dialog_name.*
import kotlinx.android.synthetic.main.dialog_name.view.*

class SettingPage : AppCompatActivity() {
    public var notification = true
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val TAG = "Settingpage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)
        auth = FirebaseAuth.getInstance()
        getDataFromDb()
        LogOutSetting.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            //getSharedPreferences("BOOT_PREF", Context.MODE_PRIVATE).edit()
             //   .putBoolean("firstboot", true).commit()
            finish()
        }
        switchDark.setOnCheckedChangeListener { buttonView, isChecked ->
            db.collection("Users").document(auth.currentUser?.email.toString())
                .update("dark", isChecked)
        }
        switchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            db.collection("Users").document(auth.currentUser?.email.toString())
                .update("notifOn", isChecked)
        }
        UserNameChange.setOnClickListener { showDiaogue()}
        DeleteAccount.setOnClickListener {
            val user = auth.currentUser!!
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.collection("Users").document(user.email.toString()).delete()
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                        getSharedPreferences("BOOT_PREF", Context.MODE_PRIVATE).edit()
                            .putBoolean("firstboot", true).commit()
                        finish()
                        Log.d(TAG, "User account deleted.")
                    }
                }
        }
    }

    private fun getDataFromDb() {
        Log.i(TAG,auth.currentUser?.email.toString()+"")
        db.collection("Users").document(auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                Log.i(TAG,"${document.data}+salut")
                switchDark.setChecked(document.getBoolean("dark")!!)
                switchNotification.setChecked(document.getBoolean("notifOn")!!)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    private fun showDiaogue(){
        val someDialogName=LayoutInflater.from(this).inflate(R.layout.dialog_name,null)
        val someBuilder =AlertDialog.Builder(this).setView(someDialogName).setTitle("Changement de nom")
        val someDialogAlert=someBuilder.show()
        someDialogName.ChangeButton.setOnClickListener{
            someDialogAlert.dismiss()
            val changedName=someDialogName.newNameText.text.toString()
            db.collection("Users").document(auth.currentUser?.email.toString()).update("name", changedName)
        }
        someDialogName.CancelButton.setOnClickListener{someDialogAlert.dismiss()}

    }
}
