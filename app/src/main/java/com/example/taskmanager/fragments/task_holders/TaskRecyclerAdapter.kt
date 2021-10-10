package com.example.taskmanager.fragments.task_holders

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.generator.TaskGenerator
import com.example.taskmanager.data.viewmodels.ProjectViewModel
import com.example.taskmanager.data.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.task_row.view.*

/**
 * An [RecyclerView.Adapter] for recyclers that handle task. Works with [R.layout.task_row]
 */
class TaskRecyclerAdapter(
    context: Context,
    /**
     * [TaskViewModel] for accessing db
     */
    private val mTaskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel,
    private val lifecycle: LifecycleOwner
    ) : RecyclerView.Adapter<TaskRecyclerAdapter.RowHolder>() {

    constructor(context: Context, databaseController: DatabaseController, lifecycle: LifecycleOwner):
            this(context, databaseController.taskViewModel,
                databaseController.projectViewModel, lifecycle)

    /**
     * Current list of tasks
     */
    private var mTasks = emptyList<Task>()

    /**
     * Current list of projects
     */
    private var mProjects = emptyList<Project>()

    /**
     * An [ArrayAdapter] for spinner in [R.layout.task_row]
     */
    private val mSpinnerAdapter = ArrayAdapter<Project>(context,
        R.layout.support_simple_spinner_dropdown_item)

    /**
     * Internal var for [filter]
     */
    private lateinit var mFilter: Filter

    /**
     * Sets filtering strategy. One of default filters will be used depends on it.
     * @see FILTER_BY_DAY
     * @see FILTER_BY_PROJECT
     */
    var filteringStrategy = -1
        set(value) {
            field = value
            when (value) {
                FILTER_BY_DAY -> { mFilter = ByDateFilter() }
                FILTER_BY_PROJECT -> { mFilter = ByProjectFilter() }
            }
        }

    /**
     * [Filter] for getting from [TaskGenerator] [List] of [Task] containing only needed tasks.
     * It is necessary to call [Filter.setCondition] to change this list.
     * @see Filter.setCondition
     */
    var filter: Filter
        get() { return mFilter }
        set(value) {
            mFilter = value
        }

    /**
     * List of all [Callback] that was registered via [registerCallback].
     * All of them will be notified if key event happens
     */
    private var mCallbacks = mutableListOf<Callback>()

    private val mTaskGenerator = TaskGenerator(lifecycle, mTaskViewModel)

    init {
        mTaskGenerator.result.observe(lifecycle) {
            setData(it)
        }
        projectViewModel.allProjects.observe(lifecycle) {
            setProjects(it)
        }
    }

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        return RowHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent,
        false))
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val currentItem = mTasks[position]

        //set name field
        holder.itemView.taskRow_name.text = currentItem.name
        //set date field
        holder.itemView.taskRow_date.text = currentItem.date.toString()
        //set amount field or hide it if it's single task
        holder.itemView.taskRow_amount.isVisible = currentItem.amount > 0
        if (currentItem.amount > 0) {
            holder.itemView.taskRow_amount.text = currentItem.amount.toString()
        }
        //set spinner adapter and default selection
        holder.itemView.taskRow_spinner.adapter = mSpinnerAdapter
        holder.itemView.taskRow_spinner.setSelection(mSpinnerAdapter.getPosition(mProjects.find {
            return@find it.id == currentItem.projectOwnerId
        }))


        //give callback on task editing intention
        holder.itemView.taskRow_name.setOnClickListener { notifyTaskWantToBeEdited(currentItem) }

        val chk = holder.itemView.taskRow_checkBox
        //to avoid miss checked checkboxes thar appears after checking checkbox of upper task_row
        if (chk.isChecked) chk.isChecked = false
        //set check box listener
        chk.setOnCheckedChangeListener { _, isChecked ->
            //wait for animation's end
            if (isChecked) {
                Handler().postDelayed({
                    mTaskViewModel.completeTask(currentItem)
                }, 450L)
            }
        }

        //set spinner listener for changing parent project of task
        holder.itemView.taskRow_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val proj = mProjects[position]
                //check if something changed
                if (currentItem.projectOwnerId == proj.id) return

                //update task
                currentItem.projectOwnerId = proj.id
                mTaskViewModel.updateTask(currentItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }
        }
    }

    override fun getItemCount(): Int {
        return mTasks.size
    }

    private fun setData(tasks: List<Task>) {
        mTasks = tasks
        notifyDataSetChanged()
    }

    private fun setProjects(projects: List<Project>) {
        mSpinnerAdapter.clear()
        mProjects = projects
        mSpinnerAdapter.addAll(mProjects)
    }

    fun registerCallback(c: Callback) {
        mCallbacks.add(c)
    }

    private fun notifyTaskWantToBeEdited(task: Task) {
        mCallbacks.forEach {
            it.taskWantToBeEdited(task)
        }
    }

    /**
     * Instances of this class used for getting [List] of [Task] containing only needed tasks
     * from [TaskGenerator]. Inherit from this class to make custom filter. See default filters
     * for examples.
     * @see UsableForFilteringTasks
     * @see ByDateFilter
     * @see ByProjectFilter
     */
    abstract inner class Filter {
        abstract fun setCondition(cond: UsableForFilteringTasks)
    }

    private inner class ByDateFilter: Filter() {
        override fun setCondition(cond: UsableForFilteringTasks) {
            if (cond.getCondition() !is DayOfMonth)
                throw IllegalArgumentException("cond is not instance of ${DayOfMonth::class}")
            val d = cond.getCondition() as DayOfMonth
            mTaskGenerator.generateForDay(d)
        }
    }

    private inner class ByProjectFilter: Filter() {
        override fun setCondition(cond: UsableForFilteringTasks) {
            if (cond.getCondition() !is Project) {
                throw IllegalArgumentException("cond is not instance of ${Project::class}")
            }
            val p = cond.getCondition() as Project
            mTaskGenerator.generateForProjectExceptGenerated(p)
        }
    }

    abstract class Callback {
        abstract fun taskWantToBeEdited(task: Task)
    }

    companion object {
        /**
         * Use for filter tasks by date
         */
        const val FILTER_BY_DAY = 0

        /**
         * Use for filter tasks by parent project
         */
        const val FILTER_BY_PROJECT = 1
    }
}
