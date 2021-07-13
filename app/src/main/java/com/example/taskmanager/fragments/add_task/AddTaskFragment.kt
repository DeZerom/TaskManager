package com.example.taskmanager.fragments.add_task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_add_project.view.*
import kotlinx.android.synthetic.main.fragment_add_task.view.*

class AddTaskFragment : Fragment() {
    private lateinit var mTaskModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)

        mTaskModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val edit = view.addTaskFragment_editText
        val btn = view.addTaskFragment_button
        btn.setOnClickListener {
            val task = getTask(edit.text.toString())
            task?.let { mTaskModel.addTask(it) }
            //navigate back
            findNavController().popBackStack()
        }

        return view
    }

    private fun getTask(name: String): Task? {
        if (!checkName(name)) return null

        return Task(0, name)
    }

    private fun checkName(name: String): Boolean {
        Toast.makeText(requireContext(), R.string.addTaskFragment_toast_emptyName,
            Toast.LENGTH_SHORT).show()

        return name.isNotEmpty()
    }
}