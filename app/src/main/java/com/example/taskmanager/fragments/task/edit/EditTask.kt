package com.example.taskmanager.fragments.task.edit

import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.fragment_edit_task.view.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

class EditTask : Fragment() {
    /**
     * The task to edit
     */
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
        val tv = view.editTask_editName
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
        val editDate = view.editTask_editDate
        aBtn.setOnClickListener {
            //there are only Project instances in spinner
            val ownerId = (spinner.selectedItem as Project).id
            val task = createTask(tv.text.toString(), ownerId, editDate.text.toString())
            task?.let {
                mTaskViewModel.updateTask(it)
                findNavController().popBackStack()
            }
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
            }
            builder.setNegativeButton(R.string.deleting_alert_neg_btn) {_, _ ->}

            builder.create().show()
        }
        
        return view
    }

    /**
     * Creates a new [Task] with given [name], [parentProjectId], [date] and id of [mTask]. If name is incorrect,
     * it will return null. If date is incorrect, it will return null.
     * @param name for [Task.name]
     * @param parentProjectId for [Task.projectOwnerId]
     * @param date for [Task.date]
     * @return [Task] if [name] and [date] is correct. Null otherwise.
     * @see checkName
     * @see checkDate
     */
    private fun createTask(name: String, parentProjectId: Int, date: String): Task? {
        if (!checkName(name)) return null
        val localDate = checkDate(date)
        localDate?: return null

        return Task(mTask.id, name, parentProjectId, localDate)
    }

    /**
     * Checks if name is blank. Shows [Toast] if name is blank.
     * @return True if name is blank, false if name is not blank
     * @see isBlank
     * @see isNotBlank
     */
    private fun checkName(name: String): Boolean {
        if (name.isBlank()) Toast.makeText(context,
            R.string.addTaskFragment_toast_emptyName, Toast.LENGTH_SHORT).show()

        return name.isNotBlank()
    }

    /**
     * Tries to parse [date]. If successful returns [LocalDate] instance representing [date], else
     * returns null.
     * @param date date in string format.
     * @return [LocalDate] instance or null.
     * @see LocalDate.parse
     */
    private fun checkDate(date: String): LocalDate? {
        return try {
            LocalDate.parse(date)
        } catch (e: DateTimeParseException) {
            Toast.makeText(context, "Bad date", Toast.LENGTH_SHORT).show()
            null
        }
    }
}