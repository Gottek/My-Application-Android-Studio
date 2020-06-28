package com.example.myapplication

import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    lateinit var messages: MutableList<Messages>
    lateinit var adapter: ChatAcitivityAdapter
    private val TAG = "Chat Activity"
    private val db = Firebase.firestore
    @RequiresApi(VERSION_CODES.O)
    val current = LocalDateTime.now()
    @RequiresApi(VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    @RequiresApi(VERSION_CODES.O)
    val formatted = current.format(formatter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()

        messages = mutableListOf<Messages>()
        db.collection("Messages").orderBy("time", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var message = document.toObject(Messages::class.java)
                    messages.add(message)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        adapter = ChatAcitivityAdapter(messages, this)
        recycler_view_messages.layoutManager = LinearLayoutManager(this)
        recycler_view_messages.adapter = adapter

        imageView_send.setOnClickListener {
            if (!editText_message.text.isEmpty()) {
                val message =
                    Messages(editText_message.text.toString(), formatted, auth.currentUser?.email!!)
                messages.add(message)
                db.collection("Messages")
                    .document(message.idMessage.toString())
                    .set(message)
                    .addOnSuccessListener { result ->
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }
            editText_message.setText("")
        }

    }

    override fun onClick(v: View?) {
    }
}
