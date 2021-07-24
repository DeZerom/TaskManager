package com.example.taskmanager.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.data.task.Task
import kotlinx.android.synthetic.main.task_row.view.*

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.RowHolder>() {
    private var mTasks = emptyList<Task>()

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        return RowHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent,
        false))
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val currentItem = mTasks[position]
        holder.itemView.taskRow_name.text = currentItem.name

        holder.itemView.taskRow_layout.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToEditTask(currentItem)
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
}