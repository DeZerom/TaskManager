package com.example.taskmanager.fragments.task_holders.day

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.home.ProjectRecyclerAdapter
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
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
        mTaskRecyclerAdapter = TaskRecyclerAdapter(requireContext())
        //set recycler adapter
        view.dayFragment_recycler.adapter = mTaskRecyclerAdapter
        //set recycler layout
        view.dayFragment_recycler.layoutManager = LinearLayoutManager(requireContext())

        //Observe tasks. Will set data to mTaskRecyclerAdapter as soon as it is possible
        mTaskViewModel.allTasks.observe(viewLifecycleOwner) {
            setDataToTaskRecycler(it)
            mTasks = it
        }

        //observe projects to put them into task_row's spinner
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mTaskRecyclerAdapter.setProjects(it)
        }

        //addTask btn
        //addTaskFragment asks for a Project to set default parent project for task in spinner view,
        //so we can provide null. AddTaskFragment will handle it
        val addTaskBtn = view.dayFragment_floatingActionButton
        addTaskBtn.setOnClickListener {
            val a = DayFragmentDirections.actionDayFragmentToAddTaskFragment(null)
            findNavController().navigate(a)
        }

        //bottom sheet listener. Hide buttons and show them
        val bottom = view.dayFragment_bottomSheet
        val bottomBehavior = BottomSheetBehavior.from(bottom)
        bottomBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        addTaskBtn.isVisible = true
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        addTaskBtn.isVisible = true
                    }
                    else -> {
                        addTaskBtn.isVisible = false
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
            //mTasks is already initialized here, so we can provide them
            setDataToTaskRecycler()
        }

        return view
    }

    /**
     * Sets data to [mTaskRecyclerAdapter]. Filters [tasks] to choose ones that's [Task.date] is
     * equal to [mDay]
     * @param tasks [List] of [Task]
     * @see LocalDate.equals
     */
    private fun setDataToTaskRecycler(tasks: List<Task> = mTasks) {
        mTaskRecyclerAdapter.setData(tasks.filter {
            return@filter it.date == mDay
        })
    }

}