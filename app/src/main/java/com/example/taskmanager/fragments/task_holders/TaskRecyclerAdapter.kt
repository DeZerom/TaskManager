package com.example.taskmanager.fragments.task_holders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.project.ProjectFragmentDirections
import kotlinx.android.synthetic.main.task_row.view.*
import java.util.ArrayList

class TaskRecyclerAdapter(context: Context): RecyclerView.Adapter<TaskRecyclerAdapter.RowHolder>() {
    private var mTasks = emptyList<Task>()
    private var mProjects = emptyList<Project>()
    private val mSpinnerAdapter = ArrayAdapter<Project>(context,
        R.layout.support_simple_spinner_dropdown_item)

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        return RowHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent,
        false))
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val currentItem = mTasks[position]

        //set name field
        holder.itemView.taskRow_name.text = currentItem.name
        //set date field
        holder.itemView.taskRow_date.text = currentItem.date.toString()
        //set spinner adapter and default selection
        holder.itemView.taskRow_spinner.adapter = mSpinnerAdapter
        holder.itemView.taskRow_spinner.setSelection(mSpinnerAdapter.getPosition(mProjects.find {
            return@find it.id == currentItem.projectOwnerId
        }))

        holder.itemView.taskRow_name.setOnClickListener {
            val action = ProjectFragmentDirections.actionProjectFragmentToEditTask(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return mTasks.size
    }

    fun setData(tasks: List<Task>) {
        mTasks = tasks
        notifyDataSetChanged()
    }

    fun setProjects(projects: List<Project>) {
        mSpinnerAdapter.clear()
        mProjects = projects
        mSpinnerAdapter.addAll(mProjects)
    }
}