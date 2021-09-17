package com.example.taskmanager.fragments

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.allViews
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_add_edit_task.view.*
import kotlinx.android.synthetic.main.fragment_add_task.view.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

class AddEditTaskBottomSheetAdapter(
    private val mBottomSheet: View,
    private val mTaskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel,
    private val mParentProjectId: Int? = null)
{
    /**
     * [List] of all [Project]
     */
    private var mProjects = emptyList<Project>()

    /**
     * Adapter for spinner
     */
    private val mSpinnerAdapter = ArrayAdapter<Project>(mBottomSheet.context,
        R.layout.support_simple_spinner_dropdown_item)

    /**
     * [Task] for editing mode
     */
    private var mTask: Task? = null

    /**
     * [Task] to edit
     */
    var task: Task?
        get() = mTask
        set(value) {
            mTask = value
            mIsEditingMode = true
            mIsAddingMode = false
        }

    private var mIsAddingMode = false
    private var mIsEditingMode = false

    private val mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet)

    init {
        //get all projects
        projectViewModel.allProjects.observeForever {
            mProjects = it
            mSpinnerAdapter.clear()
            mSpinnerAdapter.addAll(it)
        }

        //adapter for spinner
        val spinner = mBottomSheet.addEditTask_spinner
        spinner.adapter = mSpinnerAdapter

        //listener for isToday chk box
        val editDate = mBottomSheet.addEditTask_editDate
        mBottomSheet.addEditTask_chkBoxToday.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editDate.setText(LocalDate.now().toString())
                editDate.isEnabled = false
            } else {
                editDate.isEnabled = true
            }
        }

        //auto date separation
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

        //listener for isQuantitative chk box
        val editAmount = mBottomSheet.addEditTask_editAmount
        mBottomSheet.addEditTask_isQTask.setOnCheckedChangeListener { _, isChecked ->
            editAmount.isEnabled = isChecked
        }

        //listener for button
        val editName = mBottomSheet.addEditTask_editName
        val isRepeatableChkBox = mBottomSheet.addEditTask_isRepeatable
        mBottomSheet.addEditTask_button.setOnClickListener {
            //get parent project id. There are only Project instances in mSpinnerAdapter, so it will
            //convert correctly
            val parentProjectId = (spinner.selectedItem as Project).id
            //Try to create task
            val task = createTask(editName.text.toString(), parentProjectId,
                editDate.text.toString(), editAmount.text.toString(), isRepeatableChkBox.isChecked)

            //add or edit task if it exists
            task?.let {
                if (mIsAddingMode) mTaskViewModel.addTask(it)
                else mTaskViewModel.updateTask(it)
            }

            clearAllViews()
        }

        //bottom sheet callbacks
        mBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (mIsEditingMode) editingTaskMode()
                        else addingTaskMode()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //do nothing
            }
        })
    }

    /**
     * Sets initial values for all views by [mTask] properties and sets [mIsEditingMode] to true,
     * [mIsAddingMode] to false
     */
    private fun editingTaskMode() {
        mIsEditingMode = true
        mIsAddingMode = false

        mTask?.let {
            with(mBottomSheet) {
                //initial state of views
                this.addEditTask_editName.setText(it.name)
                this.addEditTask_spinner.setSelection(mSpinnerAdapter.getPosition(mProjects.find{ p ->
                    return@find p.id == it.projectOwnerId
                }))
                this.addEditTask_chkBoxToday.isChecked = it.date == LocalDate.now()
                this.addEditTask_editDate.setText(it.date.toString())
                this.addEditTask_isQTask.isChecked = it.isQuantitative
                this.addEditTask_editAmount.setText(it.amount)
                this.addEditTask_isRepeatable.isChecked = it.isRepeatable
            }
        } ?: Log.e("1234", "$this: Task is null")
    }

    /**
     * Sets default selection for spinner and [mIsAddingMode] to true, [mIsEditingMode] to false.
     */
    private fun addingTaskMode() {
        mIsAddingMode = true
        mIsEditingMode = false

        //initial state of view
        mParentProjectId?.let {
            mBottomSheet.addEditTask_spinner.setSelection(mSpinnerAdapter.getPosition(
                mProjects.find { p -> return@find p.id == it }))
        }
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
    private fun createTask(
        name: String,
        parentProjectId: Int,
        date: String,
        amount: String,
        isRepeatable: Boolean
    ): Task? {
        //name
        if (!checkName(name)) return null

        //there is no need to check parentProjectId. It takes from spinner
        //date
        val d = checkDate(date)
        d?: return null

        //amount
        val a = checkAmount(amount)
        a?: return null

        //repeat
        val repeat = if (isRepeatable) Task.REPEAT_EVERY_DAY else Task.REPEAT_NEVER

        //if everything is ok, return task
        return Task(0, name, parentProjectId, d, a, repeat)
    }

    /**
     * Checks if name is blank. Shows [Toast] if name is blank.
     * @return True if name is blank, false if name is not blank
     * @see isBlank
     * @see isNotBlank
     */
    private fun checkName(name: String): Boolean {
        if (name.isBlank()) Toast.makeText(mBottomSheet.context,
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
            Toast.makeText(mBottomSheet.context,
                R.string.taskDateInput_incorrectDate, Toast.LENGTH_SHORT).show()
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
        val toast = Toast.makeText(mBottomSheet.context,
            R.string.badAmountForTask_toast, Toast.LENGTH_SHORT)

        //if it's single task
        if (mBottomSheet.addTaskFragment_isQTask?.isChecked == false) return -1

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

    private fun clearAllViews() {
        with(mBottomSheet) {
            this.addEditTask_editName.text.clear()
            this.addEditTask_chkBoxToday.isChecked = false
            this.addEditTask_editDate.text.clear()
            this.addEditTask_isQTask.isChecked = false
            this.addEditTask_editAmount.text.clear()
            this.addEditTask_isRepeatable.isChecked = false
        }
    }
}
