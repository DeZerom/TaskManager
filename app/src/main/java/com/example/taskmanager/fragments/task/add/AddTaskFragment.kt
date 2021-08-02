package com.example.taskmanager.fragments.task.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_add_task.view.*
import java.time.LocalDate

class AddTaskFragment : Fragment() {
    /**
     * Contains default [Project] that is considered to be parent of a new task
     */
    private lateinit var mParentProject: Project

    /**
     * Adapter for spinner view
     * @see addTask_spinner
     */
    private lateinit var mSpinnerAdapter: ArrayAdapter<Project>
    private lateinit var mTaskModel: TaskViewModel
    private lateinit var mProjectViewModel: ProjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)

        //view models
        mTaskModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        mProjectViewModel = ViewModelProvider(this).get((ProjectViewModel::class.java))

        //get parent project
        val tmp = arguments?.let { AddTaskFragmentArgs.fromBundle(it).item }
        tmp?.let { mParentProject = it }

        //spinner
        val spinner = view.addTask_spinner
        mSpinnerAdapter = ArrayAdapter<Project>(requireContext(),
            R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = mSpinnerAdapter
        //set data to spinner
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mSpinnerAdapter.clear()
            mSpinnerAdapter.addAll(it)
            //set default selection
            spinner.setSelection(mSpinnerAdapter.getPosition(it.find { p ->
                return@find p.id == mParentProject.id
            }))
        }

        //add button
        val edit = view.addTaskFragment_editText
        val btn = view.addTaskFragment_button
        btn.setOnClickListener {
            val projectOwnerId = (spinner.selectedItem as Project).id
            val task = createTask(edit.text.toString(), projectOwnerId)
            task?.let { mTaskModel.addTask(it) }
            //navigate back
            //TODO may cause problems
            findNavController().popBackStack()
        }

        return view
    }

    /**
     * Creates a new [Task] with given [Task.name] and [Task.projectOwnerId]. If name is incorrect,
     * it will return Null.
     * @return [Task] if name is correct. Null otherwise.
     * @see checkName
     */
    private fun createTask(name: String, parentProjectId: Int): Task? {
        if (!checkName(name)) return null

        //TODO ask user for a Task.date
        return Task(0, name, parentProjectId, LocalDate.now())
    }

    /**
     * Checks if name is blank. Shows [Toast] if name is blank.
     * @return True if name is blank, false if name is not blank
     * @see isBlank
     * @see isNotBlank
     */
    private fun checkName(name: String): Boolean {
        if (name.isBlank()) Toast.makeText(requireContext(),
            R.string.addTaskFragment_toast_emptyName, Toast.LENGTH_SHORT).show()

        return name.isNotBlank()
    }
}