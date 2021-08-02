package com.example.taskmanager.fragments.task

import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project

class SpinnerAdapter: BaseAdapter(), SpinnerAdapter {
    private var mProjects = emptyList<Project>()

    override fun getCount(): Int {
        return mProjects.size
    }

    override fun getItem(position: Int): Any {
        return mProjects[position]
    }

    override fun getItemId(position: Int): Long {
        return mProjects[position].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return convertView
            ?: LayoutInflater.from(parent?.context).inflate(R.layout.support_simple_spinner_dropdown_item,
                parent, false)

    }

    override fun isEmpty(): Boolean {
        return mProjects.isEmpty()
    }
}