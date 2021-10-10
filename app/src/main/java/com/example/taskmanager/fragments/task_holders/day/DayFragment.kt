package com.example.taskmanager.fragments.task_holders.day

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.data.viewmodels.ProjectViewModel
import com.example.taskmanager.data.viewmodels.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_choose_date.view.*
import kotlinx.android.synthetic.main.fragment_day.view.*
import java.time.LocalDate

class DayFragment : Fragment() {
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var mTaskViewModel: TaskViewModel
    private var mDay = LocalDate.now()
    private lateinit var mTaskRecyclerAdapter: TaskRecyclerAdapter
    private lateinit var mTasks: List<Task>
    private val LOG_TAG = "1234"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_day, container, false)

        //view models
        val provider = ViewModelProvider(this)
        mProjectViewModel = provider.get(ProjectViewModel::class.java)
        mTaskViewModel = provider.get(TaskViewModel::class.java)

        //recycler adapter init
        mTaskRecyclerAdapter = TaskRecyclerAdapter(requireContext(), mTaskViewModel, mProjectViewModel,
            viewLifecycleOwner)
        mTaskRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        mTaskRecyclerAdapter.registerCallback(object : TaskRecyclerAdapter.Callback() {
            override fun taskWantToBeEdited(task: Task) {
                val fr = AddEditTaskFragment.editingMode(task)
                fr.show(parentFragmentManager, "AddEditTaskFragment_EDIT_MODE")
            }
        })
        //set recycler adapter
        mTaskRecyclerAdapter.filter.setCondition(DayOfMonth(0, mDay, false))
        view.dayFragment_recycler.adapter = mTaskRecyclerAdapter
        //set recycler layout
        view.dayFragment_recycler.layoutManager = LinearLayoutManager(requireContext())

        //addTask btn
        //addTaskFragment asks for a Project to set default parent project for task in spinner view,
        //so we can provide null. AddTaskFragment will handle it
        val addTaskBtn = view.dayFragment_addTaskFloatingButton
        addTaskBtn.setOnClickListener {
            val fr = AddEditTaskFragment.addingMode()
            fr.show(parentFragmentManager, "AddEditTaskFragment_ADD_MODE")
        }

        //calendarButton. Shows bottom sheet
        val calendarButton = view.dayFragment_calendarFloatingButton
        val bottom = view.dayFragment_bottomSheet //bottom sheet view
        val bottomBehavior = BottomSheetBehavior.from(bottom) //bottom sheet behavior
        calendarButton.setOnClickListener {
            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        //bottom sheet listener. Hide buttons and show them
        bottomBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        addTaskBtn.isVisible = true
                        calendarButton.isVisible = true
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        addTaskBtn.isVisible = true
                        calendarButton.isVisible = true
                    }
                    else -> {
                        addTaskBtn.isVisible = false
                        calendarButton.isVisible = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //do nothing
            }
        })

        //take date from bottom sheet's calendar and change data of mTaskRecyclerAdapter
        bottom.bottomSheet_calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            //month numerating starts from 0
            mDay = LocalDate.of(year, month + 1, dayOfMonth)

            //change filtering condition
            mTaskRecyclerAdapter.filter.setCondition(DayOfMonth(0, mDay, false))
        }

        return view
    }

}