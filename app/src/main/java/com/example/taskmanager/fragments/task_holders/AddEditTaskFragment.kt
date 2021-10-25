package com.example.taskmanager.fragments.task_holders

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.viewmodels.ProjectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.add_task_buttons.*
import kotlinx.android.synthetic.main.add_task_buttons.view.*
import kotlinx.android.synthetic.main.edit_task_buttons.view.*
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

    /**
     * Val for representing mode in which this fragment was called. This val always equals
     * ![mIsAddingMode].
     */
    private val mIsEditingMode: Boolean
        get() = !mIsAddingMode

    private lateinit var mSpinnerAdapter: ArrayAdapter<Project>

    private lateinit var mDatabaseController: DatabaseController

    /**
     * List of all projects from [ProjectViewModel.allProjects].
     */
    private var mProjects = emptyList<Project>()
    private var mProjectsHaventSet = true //to set default selection in spinner

    /**
     * View that holds [R.layout.add_task_buttons] or [R.layout.edit_task_buttons] depends on
     * [mIsAddingMode]
     */
    private lateinit var mAttachedButtons: View

    /**
     * Represents [Task] repeating mode. See [Task.REPEAT_NEVER] or [Task.REPEAT_EVERY_DAY], for
     * example.
     * @see Task.REPEAT_NEVER
     * @see Task.REPEAT_EVERY_DAY
     */
    private var mTaskRepeatingMode = Task.REPEAT_NEVER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_edit_task, container, false)

        //spinner adapter
        mSpinnerAdapter = ArrayAdapter(requireContext(),
            R.layout.support_simple_spinner_dropdown_item)

        mDatabaseController = DatabaseController(this)

        //observe ProjectViewModel.allProjects to know about changes in list of all projects
        mDatabaseController.projectViewModel.allProjects.observe(viewLifecycleOwner) {
            mProjects = it
            mSpinnerAdapter.clear()
            mSpinnerAdapter.addAll(mProjects)
            if (mProjectsHaventSet) { //setting default selection in spinner
                if (mTask != null) setDefaultSelection(view.addEditTaskFragment_spinner,
                    mTask.projectOwnerId) //task given => editing mode, so we provide its parentProjectId
                else if (mParentProject != null) setDefaultSelection(view.addEditTaskFragment_spinner,
                    mParentProject.id) //parentProject given => adding mode, so we provide its id
                //Neither task nor parentProject may be given, but in this case we shouldn't set
                // default selection
                mProjectsHaventSet = false
            }
        }

        mTask?.let {
            mAttachedButtons = inflater.inflate(R.layout.edit_task_buttons, null)
        } ?: run {
            mAttachedButtons = inflater.inflate(R.layout.add_task_buttons, null)
        }
        view.addEditTaskFragment_layout.addView(mAttachedButtons)

        return view
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
        val radioGroup = view.addEditTaskFragment_radioButtonGroup

        spinner.adapter = mSpinnerAdapter

        //initial state of views
        if (mIsEditingMode) {
            mTask?.let {
                editName.setText(it.name)
                isTodayChkBox.isChecked = it.date == LocalDate.now()
                editDate.setText(it.date.toString())
                isQTaskChkBox.isChecked = it.isQuantitative
                editAmount.setText(it.amount.toString()) //must be toString or it'll try to find id == amount
                isRepeatableChkBox.isChecked = it.isRepeatable
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

        //Radio buttons group listener. Finds which radio button clicked
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.addEditTaskFragment_repeatNever -> {
                    mTaskRepeatingMode = Task.REPEAT_NEVER
                }
                R.id.addEditTaskFragment_repeatEveryDay -> {
                    mTaskRepeatingMode = Task.REPEAT_EVERY_DAY
                }
                R.id.addEditTaskFragment_repeatExceptHolidays -> {
                    mTaskRepeatingMode = Task.REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS
                }
            }
        }

        //Buttons listener
        if (mAttachedButtons.id == R.id.addTaskButtons_layout) {
            view.addTaskButtons_button.setOnClickListener(mButtonOnClickListener)
        } else {
            view.editTaskButtons_apply.setOnClickListener(mButtonOnClickListener)
            view.editTaskButtons_delete.setOnClickListener(mButtonOnClickListener)
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
        amount: String
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
        val p = mDatabaseController.findParentProject(parentProjectId)
        if (!p.isForWeekend && mTaskRepeatingMode == Task.REPEAT_EVERY_DAY)
            mTaskRepeatingMode = Task.REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS
        val repeat = mTaskRepeatingMode

        //if everything is ok, return task
        return Task(id, name, parentProjectId, d, a, repeat)
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
     * Checks [amount]. If [R.id.addEditTaskFragment_isQTask] is not checked returns -1.
     * Otherwise tries to transform [amount] to [Int]. If [amount] <= 0 or [amount] is not
     * transformable to [Int] returns null.
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

    private fun setDefaultSelection(spinner: Spinner, idToFind: Int) {
        spinner.setSelection(mSpinnerAdapter.getPosition(mProjects.find {
            return@find it.id == idToFind
        }))
    }

    private val mButtonOnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return

            val spinner = view?.addEditTaskFragment_spinner
            val editName = view?.addEditTaskFragment_editName
            val editDate = view?.addEditTaskFragment_editDate
            val isQTaskChkBox = view?.addEditTaskFragment_isQTask
            val editAmount = view?.addEditTaskFragment_editAmount
            when (v.id) {
                R.id.addTaskButtons_button, R.id.editTaskButtons_apply -> {
                    //get parent project id. There are only Project instances in mSpinnerAdapter, so it will
                    //convert correctly
                    val proj = spinner?.selectedItem
                    proj?: run {
                        Toast.makeText(requireContext(),
                            "Create some projects first", Toast.LENGTH_LONG).show()
                        return
                    }

                    val parentProjectId = (proj as Project).id
                    //Try to create task
                    var task = createTask(
                        editName?.text.toString(), parentProjectId,
                        editDate?.text.toString(), isQTaskChkBox?.isChecked ?: false,
                        editAmount?.text.toString()
                    )
                    //add or edit task if it exists
                    task?.let {
                        if (mIsAddingMode) {
                            if (!mDatabaseController.addTask(it)) {
                                val builder = AlertDialog.Builder(requireContext())

                                //title
                                builder.setTitle(R.string.unableToAddTaskAlertDialog_title)
                                //message
                                builder.setMessage(R.string.unableToAddTaskAlertDialog_message)
                                //button
                                builder.setNeutralButton(R.string.ok_string) { _, _ ->}
                                builder.show()
                            }
                        } else {
                            mTask?.let { task = Task.createTaskWithAnotherId(task!!, mTask.id) }
                            if (!mDatabaseController.updateTask(it)) {
                                val builder = AlertDialog.Builder(requireContext())

                                //title
                                builder.setTitle(R.string.unableToUpdateTaskAlertDialog_title)
                                //message
                                builder.setMessage(R.string.unableToUpdateTaskAlertDialog_message)
                                //button
                                builder.setNeutralButton(R.string.ok_string) { _, _ ->}
                                builder.show()
                            }
                        }
                        this@AddEditTaskFragment.dismiss()
                    }
                }
                R.id.editTaskButtons_delete -> {
                    //alert builder
                    val builder = AlertDialog.Builder(requireContext())

                    //title
                    var tmp1 = getString(R.string.deleting_alert_title)
                    tmp1 += " ${mTask?.name}?"
                    builder.setTitle(tmp1)

                    //message
                    tmp1 = getString(R.string.deleting_alert_message)
                    tmp1 += " ${mTask?.name}?"
                    builder.setMessage(tmp1)

                    //buttons
                    builder.setPositiveButton(R.string.deleting_alert_pos_btn) {_, _ ->
                        mTask?.let { mDatabaseController.deleteTask(it) }
                        this@AddEditTaskFragment.dismiss()
                    }
                    builder.setNegativeButton(R.string.deleting_alert_neg_btn) {_, _ ->}

                    builder.create().show()
                }
            }
        }
    }
}