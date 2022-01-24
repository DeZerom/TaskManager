package com.example.taskmanager.fragments.task_holders.day

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_day.view.*
import java.time.LocalDate

class DayFragment : Fragment() {
    private lateinit var mDatabaseController: DatabaseController

    private var mDayOfMonth: DayOfMonth? = null
        set(value) {
            field = value
            Log.i("1234", field?.date?.toString() ?: "null")
            mTaskRecyclerAdapter.filter.setCondition(value ?: TaskRecyclerAdapter
                .EMPTY_FILTERING_CONDITION)
        }

    private lateinit var mTaskRecyclerAdapter: TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabaseController = DatabaseController(this)

        return inflater.inflate(R.layout.fragment_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.dayFragment_recycler
        val addTaskBtn = view.dayFragment_addTaskFloatingButton
        val chooseDateBtn = view.dayFragment_calendarFloatingButton

        //recycler adapter init
        mTaskRecyclerAdapter = TaskRecyclerAdapter(requireContext(), mDatabaseController,
            viewLifecycleOwner)
        mTaskRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        mTaskRecyclerAdapter.registerCallback(object : TaskRecyclerAdapter.Callback() {
            override fun taskWantToBeEdited(task: Task) {
                val fr = AddEditTaskFragment.editingMode(task)
                fr.show(parentFragmentManager, "AddEditTaskFragment_EDIT_MODE")
            }
            override fun taskWantToChangeItsDate(task: Task) {
                val fr = ChooseDateFragment.changeDateInTask(task)
                fr.show(parentFragmentManager, fr.tag)
            }
        })
        //first condition setting
        mDatabaseController.whenTasksLoaded = {
            mTaskRecyclerAdapter.filter.setCondition(mDayOfMonth ?: TaskRecyclerAdapter
                .EMPTY_FILTERING_CONDITION)
        }

        //set recycler adapter
        recycler.adapter = mTaskRecyclerAdapter
        //set recycler layout
        recycler.layoutManager = LinearLayoutManager(requireContext())

        //addTask btn listener
        addTaskBtn.setOnClickListener {
            val fr = AddEditTaskFragment.addingMode()
            fr.show(parentFragmentManager, "AddEditTaskFragment_ADD_MODE")
        }

        //chooseDate logic
        chooseDateBtn.setOnClickListener {
            val f = ChooseDateFragment.chooseDate(mDayOfMonth?.date ?: LocalDate.now())
            f.listener = dateChangedListener
            f.show(parentFragmentManager, f.tag)
        }
    }

    private val dateChangedListener = object: ChooseDateFragment.DateChangedListener {
        override fun onDateChangeListener(oldDate: LocalDate, newDate: LocalDate?) {
            newDate?.let {
                mDayOfMonth = mDatabaseController.getDay(it)
            } ?: run {
                mDayOfMonth = null
            }
        }
    }
}
