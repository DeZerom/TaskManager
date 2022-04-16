package com.example.taskmanager.fragments.task_holders

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getColor
import kotlin.Pair
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
import kotlinx.android.synthetic.main.task_row.view.*

/**
 * An [RecyclerView.Adapter] for recyclers that handle task. Works with [R.layout.task_row]
 */
class TaskRecyclerAdapter(
    context: Context,
    private val mDatabaseController: DatabaseController,
    lifecycle: LifecycleOwner
    ) : RecyclerView.Adapter<TaskRecyclerAdapter.RowHolder>() {

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
    private val mFilter = Filter()

    /**
     * Sets filtering strategy. One of default filters will be used depends on it.
     * @see FILTER_BY_DAY
     * @see FILTER_BY_PROJECT
     * @see FILTER_BY_DAY_AND_PROJECT
     */
    var filteringStrategy = -1

    /**
     * [Filter] for getting from [TaskGenerator] [List] of [Task] containing only needed tasks.
     * It is necessary to call [Filter.setCondition] to change this list.
     * @see Filter.setCondition
     */
    val filter: Filter
        get() { return mFilter }

    /**
     * List of all [Callback] that was registered via [registerCallback].
     * All of them will be notified if key event happens
     */
    private var mCallbacks = mutableListOf<Callback>()

    init {
        mDatabaseController.taskGenerator.result.observe(lifecycle) {
            setData(it)
        }
        mDatabaseController.projectViewModel.allProjects.observe(lifecycle) {
            setProjects(it)
        }
    }

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView)

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


        //check if task is overdue
        if (currentItem.isOverdue) {
            //if overdue then mark it
            holder.itemView.taskRow_date
                .setTextColor(getColor(holder.itemView.context, R.color.myRed))
        } else {
            holder.itemView.taskRow_date
                .setTextColor(getColor(holder.itemView.context, R.color.myBlack))
        }

        //give callback on task editing intention
        holder.itemView.taskRow_name.setOnClickListener { notifyTaskWantToBeEdited(currentItem) }
        //give callback on intention to change task's date
        holder.itemView.taskRow_date.setOnClickListener { notifyTaskWantToChangeDate(currentItem) }

        val chk = holder.itemView.taskRow_checkBox
        //to avoid miss checked checkboxes thar appears after checking checkbox of upper task_row
        if (chk.isChecked) chk.isChecked = false
        //set check box listener
        chk.setOnCheckedChangeListener { _, isChecked ->
            //wait for animation's end
            if (isChecked) {
                Handler().postDelayed({
                    mDatabaseController.completeTask(currentItem)
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
                val task = Task.Companion.createTaskWithAnotherProjectOwner(currentItem, proj)
                mDatabaseController.updateTask(task)
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

    private fun notifyTaskWantToChangeDate(task: Task) {
        mCallbacks.forEach{
            it.taskWantToChangeItsDate(task)
        }
    }

    /**
     * Instances of this class used for getting [List] of [Task] containing only needed tasks
     * from [TaskGenerator].
     * @see UsableForFilteringTasks
     */
    inner class Filter {
        private val condTransformer = ConditionsTransformer()

        fun setCondition(vararg conditions: UsableForFilteringTasks) {
            if (conditions.isEmpty()) {
                tryToGenerateForEmptyCondition()
                return
            }
            if (conditions.size == 1 && conditions[0] is EmptyFilteringCondition) {
                tryToGenerateForEmptyCondition()
                return
            }

            when (filteringStrategy) {
                FILTER_BY_DAY -> {
                    val day = condTransformer.getDayOfMonthFromConditions(conditions)
                    mDatabaseController.generateForDay(day)
                }
                FILTER_BY_PROJECT -> {
                    val proj = condTransformer.getProjectFromConditions(conditions)
                    mDatabaseController.generateForProjectExceptGenerated(proj)
                }
                FILTER_BY_DAY_AND_PROJECT -> {
                    val pair = condTransformer.getProjectAndDayOfMonthFromConditions(conditions)
                    val project = pair.first
                    val dayOfMonth = pair.second
                    mDatabaseController.generateForProjectAndDayOfMonth(project, dayOfMonth)
                }
            }

        }

        private fun tryToGenerateForEmptyCondition() {
            when (filteringStrategy) {
                FILTER_BY_DAY -> { mDatabaseController.generateForDay(null) }
                FILTER_BY_PROJECT -> { throw NullPointerException("It's impossible to filter by " +
                        "parent project with null ${Project::class.java} instance") }
                FILTER_BY_DAY_AND_PROJECT -> { throw NullPointerException("It's impossible to " +
                        "filter by parent project with null ${Project::class.java} instance") }
            }
        }
    }

    private class ConditionsTransformer {
        fun getDayOfMonthFromConditions(conditions: Array<out UsableForFilteringTasks>): DayOfMonth {
            //check size
            if (conditions.size != 1) throw IllegalArgumentException("Wrong arguments amount. " +
                    "Expected 1, but get ${conditions.size}")

            val cond = conditions[0].getCondition()
            return cond as DayOfMonth
        }

        fun getProjectFromConditions(conditions: Array<out UsableForFilteringTasks>): Project {
            if (conditions.size != 1) throw IllegalArgumentException("Wrong arguments amount. " +
                    "Expected 1, but get ${conditions.size}")

            val cond = conditions[0].getCondition()
            return cond as Project
        }

        fun getProjectAndDayOfMonthFromConditions(
            conditions: Array<out UsableForFilteringTasks>
        ): Pair<Project, DayOfMonth> {
            //check size
            if (conditions.size != 2) throw IllegalArgumentException("Wrong arguments amount. " +
                    "Expected 2, but get ${conditions.size}")

            var day: DayOfMonth? = null
            var project: Project? = null
            try {
                day = getDayOfMonthFromConditions(arrayOf(conditions[0]))
                project = getProjectFromConditions(arrayOf(conditions[1]))
            } catch (e: ClassCastException) {
                //try another order
                day = getDayOfMonthFromConditions(arrayOf(conditions[1]))
                project = getProjectFromConditions(arrayOf(conditions[0]))
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

            if (day != null && project != null) {
                return Pair(project, day)
            } else {
                throw IllegalArgumentException("Some shit have been provided in params")
            }
        }
    }

    class EmptyFilteringCondition: UsableForFilteringTasks {
        override fun getCondition(): Any {
            return this
        }
    }

    abstract class Callback {
        abstract fun taskWantToBeEdited(task: Task)
        abstract fun taskWantToChangeItsDate(task: Task)
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

        /**
         * Use for filtering by parent project and date
         */
        const val FILTER_BY_DAY_AND_PROJECT = 2

        val EMPTY_FILTERING_CONDITION = EmptyFilteringCondition()
    }
}
