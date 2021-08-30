package com.example.taskmanager.fragments.task_holders.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import java.time.format.DateTimeParseException

class AddTaskFragment : Fragment() {
    /**
     * Contains default [Project] that is considered to be parent of a new task
     */
    private var mParentProject: Project? = null

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
        val provider = ViewModelProvider(this)
        mTaskModel = provider.get(TaskViewModel::class.java)
        mProjectViewModel = provider.get((ProjectViewModel::class.java))

        //get parent project
        mParentProject = arguments?.let { AddTaskFragmentArgs.fromBundle(it).item }

        //spinner
        val spinner = view.addTask_spinner
        mSpinnerAdapter = ArrayAdapter<Project>(requireContext(),
            R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = mSpinnerAdapter
        //set data to spinner
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mSpinnerAdapter.clear()
            mSpinnerAdapter.addAll(it)
            //set default selection if mParentProject is not null
            mParentProject?.let { pp ->
                spinner.setSelection(mSpinnerAdapter.getPosition(it.find { p ->
                    return@find p.id == pp.id
                }))
            }
        }

        //is today chk box logic
        val chk = view.addTask_chkBoxToday
        val editDate = view.addTaskFragment_editDate
        chk.setOnClickListener {
            if (chk.isChecked) {
                editDate.setText(LocalDate.now().toString())
                editDate.isEnabled = false
            } else {
                editDate.isEnabled = true
            }
        }

        //auto date separators
        editDate.addTextChangedListener( object : TextWatcher {
            private var isAdded = false
            private var lBefore = -1

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.let { lBefore = it.length }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    isAdded = lBefore < it.length
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    //str is not null
                    if (isAdded) {
                        when (s.length) {
                            5 -> {
                                s.insert(4, "-")
                            }
                            8 -> {
                                s.insert(7, "-")
                            }
                        }
                    }
                }
            }
        })

        //is quantitative chk box logic
        val isQuantitative = view.addTaskFragment_isQTask
        val editAmount = view.addTaskFragment_editAmount
        isQuantitative.setOnCheckedChangeListener { _, isChecked ->
            editAmount.isEnabled = isChecked
        }

        //add new task
        val btn = view.addTaskFragment_button
        val editName = view.addTaskFragment_editName
        val repeatChkBtn = view.addTaskFragment_isRepeatable
        btn.setOnClickListener {
            val projectOwnerId = (spinner.selectedItem as Project).id
            val amount = editAmount.text.toString()

            //try to create task
            val task = createTask(editName.text.toString(), projectOwnerId,
                editDate.text.toString(), amount, repeatChkBtn.isChecked)

            //add it to the db if it's not null
            task?.let {
                mTaskModel.addTask(it)
                //navigate back
                findNavController().popBackStack()
            }
        }

        return view
    }

    /**
     * Creates a new [Task] with given [name], [parentProjectId], [date], [amount]. If at least one
     * of params is incorrect returns null.
     * @param name for [Task.name]
     * @param parentProjectId for [Task.projectOwnerId]
     * @param date for [Task.date]
     * @param amount for [Task.amount]
     * @return [Task] if [name], [date] and [amount] is correct. Null otherwise.
     * @see checkName
     * @see checkDate
     * @see checkAmount
     */
    private fun createTask(name: String,
                           parentProjectId: Int,
                           date: String,
                           amount: String,
                           isRepeatable: Boolean): Task?
    {
        //name
        if (!checkName(name)) return null

        //date
        val localDate = checkDate(date)
        localDate?: return null

        //amount
        val intAmount = checkAmount(amount)
        intAmount ?: return null

        //repeat
        val repeat = if (isRepeatable) Task.REPEAT_EVERY_DAY else Task.REPEAT_NEVER

        return Task(0, name, parentProjectId, localDate, intAmount, repeat)
    }

    /**
     * Checks if name is blank. Shows [Toast] if name is blank.
     * @return True if name is blank, false if name is not blank
     * @see isBlank
     * @see isNotBlank
     */
    private fun checkName(name: String): Boolean {
        if (name.isBlank()) Toast.makeText(context,
            R.string.taskNameInput_blankName, Toast.LENGTH_SHORT).show()

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
            Toast.makeText(context, R.string.taskDateInput_incorrectDate, Toast.LENGTH_SHORT).show()
            null
        }
    }

    /**
     * Checks [amount]. If [R.id.addTaskFragment_isQTask] is not checked returns -1. Otherwise tries
     * to transform [amount] to [Int]. If [amount] <= 0 or [amount] is not transformable to [Int]
     * returns null.
     * @param amount amount to check
     * @return null, [amount] transformed to [Int] that bigger than 0 or -1
     * @see toIntOrNull
     */
    private fun checkAmount(amount: String): Int? {
        val toast = Toast.makeText(context,
            R.string.badAmountForTask_toast, Toast.LENGTH_SHORT)

        //if it's single task
        if (view?.addTaskFragment_isQTask?.isChecked == false) return -1

        //try to transform
        val intAmount = amount.toIntOrNull()
        //check amount
        intAmount?.let {
            if (it <= 0) {
                toast.show()
                return null
            }
        } ?: run {
            toast.show()
            return null
        }

        //if everything is OK
        return intAmount
    }
}