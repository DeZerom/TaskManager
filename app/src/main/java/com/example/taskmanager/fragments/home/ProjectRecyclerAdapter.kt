package com.example.taskmanager.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
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
        //set project name text
        holder.itemView.projectRow_name.text = currentItem.name
        //set nav functional
        holder.itemView.projectRow_layout.setOnClickListener {
            val a = HomeFragmentDirections.actionGlobalProjectFragment(currentItem)
            holder.itemView.findNavController().navigate(a)
        }
    }

    override fun getItemCount(): Int {
        return mProjects.size
    }

    fun setData(projects: List<Project>) {
        mProjects = projects
        notifyDataSetChanged()
    }

}