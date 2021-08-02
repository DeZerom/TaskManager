package com.example.taskmanager.fragments.task.edit

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_edit_task.view.*

class EditTask : Fragment() {
    private lateinit var mTask: Task
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var mSpinnerAdapter: ArrayAdapter<Project>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)

        //get arguments
        val tmp = arguments?.let { EditTaskArgs.fromBundle(it).item }
        tmp?.let { mTask = it }

        //get view models
        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        mProjectViewModel = ViewModelProvider(this).get(ProjectViewModel::class.java)

        //set text to edit fields
        val tv = view.editTask_editText
        tv.setText(mTask.name)

        //spinner
        val spinner = view.editTask_spinner
        mSpinnerAdapter = ArrayAdapter<Project>(requireContext(),
            R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = mSpinnerAdapter
        //set data to spinner
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mSpinnerAdapter.clear()
            mSpinnerAdapter.addAll(it)
            //set default selection
            spinner.setSelection(mSpinnerAdapter.getPosition(it.find { p ->
                return@find p.id == mTask.projectOwnerId
            }))
        }

        //apply btn
        val aBtn = view.editTask_applyButton
        aBtn.setOnClickListener {
            //there are only Project instances in spinner
            val ownerId = (spinner.selectedItem as Project).id
            val task = Task(mTask.id, tv.text.toString(), ownerId)
            mTaskViewModel.updateTask(task)
            findNavController().popBackStack()
        }

        //delete btn
        val dBtn = view.editTask_deleteButton
        dBtn.setOnClickListener {
            //alert builder
            val builder = AlertDialog.Builder(requireContext())

            //title
            var tmp1 = getString(R.string.deleting_alert_title)
            tmp1 += " ${mTask.name}?"
            builder.setTitle(tmp1)

            //message
            tmp1 = getString(R.string.deleting_alert_message)
            tmp1 += " ${mTask.name}?"
            builder.setMessage(tmp1)

            //buttons
            builder.setPositiveButton(R.string.deleting_alert_pos_btn) {_, _ ->
                mTaskViewModel.deleteTask(mTask)
                findNavController().popBackStack()
                //TODO popBackStack may cause problems
            }
            builder.setNegativeButton(R.string.deleting_alert_neg_btn) {_, _ ->}

            builder.create().show()
        }
        
        return view
    }
}