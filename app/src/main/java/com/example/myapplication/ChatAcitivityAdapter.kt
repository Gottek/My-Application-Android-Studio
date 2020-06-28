package com.example.myapplication

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_text_message.view.*

class ChatAcitivityAdapter (val Messages: List<Messages>, val itemClickListener: View.OnClickListener)
: RecyclerView.Adapter<ChatAcitivityAdapter.ViewHolder>() {

    private val db = Firebase.firestore

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById(R.id.message_cardview) as CardView
        val messageText = cardView.findViewById(R.id.textView_message_text) as TextView
        val messageDate = cardView.findViewById(R.id.textView_message_time) as TextView
        val messageAuth = cardView.findViewById(R.id.textView_message_id) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text_message, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val MessageTab = Messages[position]
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
        holder.cardView.id = MessageTab.idMessage
        holder.cardView.tag = position
        holder.messageText.text = MessageTab.theText
        holder.messageDate.text = MessageTab.time
        holder.messageAuth.text = MessageTab.mailUser
        if (MessageTab.mailUser == FirebaseAuth.getInstance().currentUser?.email) {
            holder.cardView.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_white)
                val lParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT, Gravity.END)
                this.layoutParams = lParams
            }
        }
        else {
            holder.cardView.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_primary_color)
                val lParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT,Gravity.START)
                this.layoutParams = lParams
            }
        }

    }

    override fun getItemCount(): Int {
        return Messages.size
    }
}