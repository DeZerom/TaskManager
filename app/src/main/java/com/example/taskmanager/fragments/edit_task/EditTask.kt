package com.example.taskmanager.fragments.edit_task

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_edit_task.view.*

class EditTask : Fragment() {
    private lateinit var mTask: Task
    private lateinit var mTaskViewModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)

        //get arguments
        val tmp = arguments?.let { EditTaskArgs.fromBundle(it).item }
        tmp?.let { mTask = it }

        //get model
        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        //set text to edit fields
        val tv = view.editTask_editText
        tv.setText(mTask.name)

        //apply btn
        val aBtn = view.editTask_applyButton
        aBtn.setOnClickListener {
            val task = Task(mTask.id, tv.text.toString())
            mTaskViewModel.updateTask(task)
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
                //TODO may cause problems
            }
            builder.setNegativeButton(R.string.deleting_alert_neg_btn) {_, _ ->}

            builder.create().show()
        }
        
        return view
    }
}