import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*


class ProjectAdapter(
    val projects: List<Projects>,
    val itemClickListener: ProjectAdapter.ProjectItemListener
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>(), View.OnClickListener {

    interface ProjectItemListener {
        fun onProjectSelected(project: Projects, position: Int)
        fun onProjectDeleted(project: Projects, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById(R.id.project_cardview) as CardView
        val titleView = cardView.findViewById(R.id.project_item_title) as TextView
        val excerptView = cardView.findViewById(R.id.project_item_extrait) as TextView
        val stateView = cardView.findViewById(R.id.textViewStat) as TextView
        val PoubelleProject = itemView.findViewById<View>(R.id.PoubelleProject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.projet_item, parent, false)
        return ViewHolder(viewItem)
    }

    private val db = Firebase.firestore

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var statue = true
        val projectTab = projects[position]
        holder.cardView.setOnClickListener(this@ProjectAdapter)
        holder.cardView.setTag(R.string.position_project, position)
        holder.cardView.setTag(R.string.pour_le_project, projectTab)
        holder.PoubelleProject.setTag(R.string.position_project, position)
        holder.PoubelleProject.setTag(R.string.pour_le_project, projectTab)
        holder.titleView.text = projectTab.title
        holder.excerptView.text = projectTab.description
        holder.PoubelleProject.setOnClickListener(this@ProjectAdapter)

        db.collection("Sections").whereEqualTo("idProjects", projectTab.idProject.hashCode().toString()).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                   var section= document.toObject(Sections::class.java)
                    //Log.i("Main Activitu")
                    if (!section.state) statue = false
                }
                db.collection("Projects").document(projectTab.idProject.hashCode().toString()).update("state",statue).addOnSuccessListener {
                    projectTab.state=statue
                holder.stateView.text = if (projectTab.state) "finished" else " unfinished"
                if (projectTab.state) holder.stateView.setTextColor(Color.parseColor("#27ae60"))
                else holder.stateView.setTextColor(Color.parseColor("#e74c3c"))
                }
            }

    }

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.project_cardview -> itemClickListener.onProjectSelected(
                v.getTag(R.string.pour_le_project) as Projects,
                v.getTag(R.string.position_project) as Int
            )
            R.id.PoubelleProject -> itemClickListener.onProjectDeleted(
                v.getTag(R.string.pour_le_project) as Projects,
                v.getTag(R.string.position_project) as Int
            )
        }
    }


}