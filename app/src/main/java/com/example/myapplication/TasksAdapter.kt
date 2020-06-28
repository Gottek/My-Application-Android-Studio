package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(val tasks: List<Tasks>, val itemClickListener: View.OnClickListener)
: RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById(R.id.item_cardview) as CardView
        val checkBoxItem = cardView.findViewById(R.id.checkBoxItem) as CheckBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return ViewHolder(viewItem)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TaskTab = tasks[position]
        holder.checkBoxItem.setOnClickListener(itemClickListener)
        holder.checkBoxItem.tag = TaskTab
        holder.checkBoxItem.id=TaskTab.idTask
        holder.checkBoxItem.text=TaskTab.text
        holder.checkBoxItem.isChecked = TaskTab.isDone
    }
}