package com.example.taskmanager.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import kotlinx.android.synthetic.main.project_row.view.*

class ProjectRecyclerAdapter: RecyclerView.Adapter<ProjectRecyclerAdapter.RowHolder>() {
    private var mProjects = emptyList<Project>()

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        return RowHolder(LayoutInflater.from(parent.context).inflate(R.layout.project_row,
        parent, false))
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val currentItem = mProjects[position]
        holder.itemView.projectRow_name.text = currentItem.name
    }

    override fun getItemCount(): Int {
        return mProjects.size
    }

    fun setData(projects: List<Project>) {
        mProjects = projects
        notifyDataSetChanged()
    }

}