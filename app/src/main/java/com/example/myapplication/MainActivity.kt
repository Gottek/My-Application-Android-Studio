package com.example.myapplication

import ProjectAdapter
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ProjectAdapter.ProjectItemListener {
    lateinit var projects: MutableList<Projects>
    lateinit var adapter: ProjectAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userPrem: Users;
    private val db = Firebase.firestore
    private val TAG = "Main Activity"
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    public lateinit var sharedPreferences: SharedPreferences
    private var channelId = "com.example.myapplication"
    private var description = "notification de rappel"
    private lateinit var value:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar) // pour activier la toolbar personnalisé au lieu de l'ancienne toolbar
        auth = FirebaseAuth.getInstance()
        val dbRef = db.collection("Users").document(auth.currentUser?.email.toString())
        dbRef.get()
            .addOnSuccessListener { result ->
                NomNavbar.text = result.getString("last")
                AdresseNavbar.text = auth.currentUser?.email.toString()
                this.userPrem = Users(
                    result.getString("name").toString(),
                    result.getString("last").toString(),
                    result.getString("phone").toString(),
                    result.getBoolean("dark"),
                    result.getBoolean("notifOn")
                )
                Log.i(TAG," voici les informations ${this.userPrem} ")
               fetchMessageOnStart(this.userPrem)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, 0, 0
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        //project_fab.setOnClickListener(this)
        projects = mutableListOf<Projects>()
        db.collection("Projects").whereEqualTo("mailUser", auth.currentUser?.email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var project = document.toObject(Projects::class.java)
                    projects.add(project)
                    adapter.notifyDataSetChanged()
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        adapter = ProjectAdapter(projects, this)
        project_list_recyclerView.layoutManager = LinearLayoutManager(this)
        project_list_recyclerView.adapter = adapter


        project_fab.setOnClickListener {
            creatNewProject()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfilePage::class.java)
                intent.putExtra("InformationProfile", userPrem)
                startActivity(intent)
            }
            R.id.nav_projects -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_message -> {
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_setting -> {
                val intent = Intent(this, SettingPage::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                getSharedPreferences("BOOT_PREF", Context.MODE_PRIVATE).edit().putBoolean("firstboot", true).commit()
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        } else {
            val project = data.getParcelableExtra<Projects>("NewProject")
            Log.i(TAG, "${project.idProject}")
            projects.add(project)
            db.collection("Projects")
                .document(project.idProject.hashCode().toString())
                .set(project)
                .addOnSuccessListener { result ->
                    Log.d(TAG, "Project ${project.title} add successfully")
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }


    }

    private fun creatNewProject() {
        val intent = Intent(this, CreatePageProject::class.java)
        startActivityForResult(intent, 0)
    }

    override fun onProjectSelected(project: Projects, position: Int) {
        val intent = Intent(this, SectionPage::class.java)
        intent.putExtra("idProject", project.idProject.hashCode().toString())
        startActivity(intent)
    }

    override fun onProjectDeleted(project: Projects, position: Int) {
        db.collection("Projects").document(project.idProject.hashCode().toString())
            .delete()
            .addOnSuccessListener {
                Log.d(
                    "MAIN ACTIVITY",
                    "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w("MAIN ACTIVITY", "Error deleting document", e) }
        projects.removeAt(position)
        adapter.notifyDataSetChanged()
    }

    private fun onComing(nameSender:String, contentMessage:String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext, TaskPage::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = Notification.Builder(this, channelId)
                .setContentTitle("Nouveau message")
                .setContentText("$nameSender : $contentMessage")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setContentTitle("Nouveau message")
                .setContentText("$nameSender : $contentMessage")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(0, builder.build())
    }

    private fun fetchMessageOnStart(user: Users){
        val firstboot: Boolean = getSharedPreferences("BOOT_PREF", Context.MODE_PRIVATE).getBoolean("firstboot", true)
        db.collection("Messages").orderBy("time", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener {
                documents->
            for(document in documents){
                var message=document.toObject(Messages::class.java)
            if (firstboot&&(message.mailUser!=auth.currentUser?.email.toString())&&user.isNotifOn!!) {
                onComing("${message.mailUser}","${message.theText}")
                getSharedPreferences("BOOT_PREF", Context.MODE_PRIVATE).edit().putBoolean("firstboot", false).commit()
            }
            Log.i(TAG,"voici le message hihi :${document.data}")
            }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}
