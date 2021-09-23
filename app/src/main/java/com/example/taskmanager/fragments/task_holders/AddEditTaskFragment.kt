package com.example.taskmanager.fragments.task_holders

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_add_edit_task.view.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Create instances via [AddEditTaskFragment.addingMode] or [AddEditTaskFragment.editingMode]
 * @see [AddEditTaskFragment.addingMode]
 * @see [AddEditTaskFragment.editingMode]
 */
class AddEditTaskFragment(
    private val mIsAddingMode: Boolean,
    private val mTask: Task? = null,
    private val mParentProject: Project? = null
) : BottomSheetDialogFragment() {

    private val mIsEditingMode: Boolean
        get() = !mIsAddingMode

    private lateinit var mSpinnerAdapter: ArrayAdapter<Project>

    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mProjectViewModel: ProjectViewModel

    private var mProjects = emptyList<Project>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //spinner adapter
        mSpinnerAdapter = ArrayAdapter(requireContext(),
            R.layout.support_simple_spinner_dropdown_item)

        //view models
        val provider = ViewModelProvider(this)
        mTaskViewModel = provider.get(TaskViewModel::class.java)
        mProjectViewModel = provider.get(ProjectViewModel::class.java)

        //observe ProjectViewModel.allProjects to know about changes in list of all projects
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mProjects = it
            mSpinnerAdapter.clear()
            mSpinnerAdapter.addAll(mProjects)
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //views
        val editName = view.addEditTaskFragment_editName
        val spinner = view.addEditTaskFragment_spinner
        val isTodayChkBox = view.addEditTaskFragment_chkBoxToday
        val editDate = view.addEditTaskFragment_editDate
        val isQTaskChkBox = view.addEditTaskFragment_isQTask
        val editAmount = view.addEditTaskFragment_editAmount
        val isRepeatableChkBox = view.addEditTaskFragment_chkBoxToday
        val button = view.addEditTaskFragment_button

        spinner.adapter = mSpinnerAdapter

        //initial state of views
        if (mIsEditingMode) {
            mTask?.let {
                editName.setText(it.name)
                spinner.setSelection(mSpinnerAdapter.getPosition(mProjects.find { p ->
                    return@find p.id == it.projectOwnerId }))
                isTodayChkBox.isChecked = it.date == LocalDate.now()
                editDate.setText(it.date.toString())
                isQTaskChkBox.isChecked = it.isQuantitative
                editAmount.setText(it.amount)
                isRepeatableChkBox.isChecked = it.isRepeatable
            }
        } else {
            mParentProject?.let {
                spinner.setSelection(mSpinnerAdapter.getPosition(mParentProject))
            }
        }

        //listeners
        //enables or disables editDate. If checked sets editDate.text to LocalDate.now
        isTodayChkBox.setOnCheckedChangeListener { _, isChecked ->
            editDate.isEnabled = !isChecked
            if (isChecked) editDate.setText(LocalDate.now().toString())
        }

        //Separates parts of date
        editDate.addTextChangedListener(mAutoDateSeparator)

        //Enables or disables editAmount
        isQTaskChkBox.setOnCheckedChangeListener { _, isChecked ->
            editAmount.isEnabled = isChecked
        }

        //Creates new task
        button.setOnClickListener {
            //get parent project id. There are only Project instances in mSpinnerAdapter, so it will
            //convert correctly
            val parentProjectId = (spinner.selectedItem as Project).id
            //Try to create task
            val task = createTask(editName.text.toString(), parentProjectId,
                editDate.text.toString(), isQTaskChkBox.isChecked, editAmount.text.toString(),
                isRepeatableChkBox.isChecked)

            //add or edit task if it exists
            task?.let {
                if (mIsAddingMode) {
                    mTaskViewModel.addTask(it)
                }
                else {
                    mTaskViewModel.updateTask(it)
                }
                this.dismiss()
            }
        }
    }

    companion object {
        fun addingMode(parentProject: Project? = null): AddEditTaskFragment {
            return AddEditTaskFragment(true, mParentProject = parentProject)
        }
        fun editingMode(task: Task): AddEditTaskFragment {
            return AddEditTaskFragment(false, mTask = task)
        }
    }

    /**
     * [TextWatcher] for [addEditTaskFragment_editDate]. Needed for separate date parts while
     * entering date into [addEditTaskFragment_editDate]
     */
    private val mAutoDateSeparator = object : TextWatcher {
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
        isQuantitative: Boolean,
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
        var a: Int? = -1
        if (isQuantitative) a = checkAmount(amount)
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
        if (name.isBlank()) Toast.makeText(requireContext(),
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
            Toast.makeText(requireContext(),
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
        val toast = Toast.makeText(requireContext(),
            R.string.badAmountForTask_toast, Toast.LENGTH_SHORT)

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