import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Sections
import com.example.myapplication.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SectionAdapter(
    val sections: List<Sections>,
    val itemClickListener: SectionAdapter.sectionItemListener
) :
    RecyclerView.Adapter<SectionAdapter.ViewHolder>(), View.OnClickListener {

    interface sectionItemListener {
        fun onSectionSelected(section: Sections, position: Int)
        fun onSectionDeleted(section: Sections, position: Int)
    }

    private val db = Firebase.firestore

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById(R.id.section_cardview) as CardView
        val titleView = cardView.findViewById(R.id.section_item_title) as TextView
        val excerptView = cardView.findViewById(R.id.section_item_extrait) as TextView
        val stateView = cardView.findViewById(R.id.textViewStatSection) as TextView
        val PoubelleSection = itemView.findViewById<View>(R.id.PoubelleSection)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.section_item, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var statue = true
        val sectionTab = sections[position]
        holder.cardView.setOnClickListener(this@SectionAdapter)
        holder.cardView.setTag(R.string.position, position)
        holder.cardView.setTag(R.string.pour_la_section, sectionTab)
        holder.titleView.text = sectionTab.title
        holder.excerptView.text = sectionTab.description
        holder.PoubelleSection.setTag(R.string.position, position)
        holder.PoubelleSection.setTag(R.string.pour_la_section, sectionTab)
        holder.PoubelleSection.setOnClickListener(this@SectionAdapter)

        db.collection("Tasks").whereEqualTo("idSection", sectionTab.idSection).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var task= document.toObject(Tasks::class.java)
                    if (!task.isDone) statue = false
                }
                db.collection("Sections").document(sectionTab.idSection.toString()).update("state",statue).addOnSuccessListener {
                    sectionTab.state=statue
                    holder.stateView.text = if (sectionTab.state) "finished" else " unfinished"
                    if (sectionTab.state) holder.stateView.setTextColor(Color.parseColor("#27ae60"))
                    else holder.stateView.setTextColor(Color.parseColor("#e74c3c"))
                }
            }


    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.section_cardview -> itemClickListener.onSectionSelected(
                v.getTag(R.string.pour_la_section) as Sections,
                v.getTag(R.string.position) as Int
            )
            R.id.PoubelleSection -> itemClickListener.onSectionDeleted(
                v.getTag(R.string.pour_la_section) as Sections,
                v.getTag(R.string.position) as Int
            )
        }
    }


}

