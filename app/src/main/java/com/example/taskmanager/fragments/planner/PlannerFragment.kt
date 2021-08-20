package com.example.taskmanager.fragments.planner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_choose_date.view.*
import kotlinx.android.synthetic.main.fragment_planner.view.*
import java.time.LocalDate

class PlannerFragment : Fragment() {
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter
    private var mTasks = emptyList<Task>()
    private var mDay = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_planner, container, false)

        //view models
        val provider = ViewModelProvider(this)
        mTaskViewModel = provider.get(TaskViewModel::class.java)
        mProjectViewModel = provider.get(ProjectViewModel::class.java)

        //recycler
        mRecyclerAdapter = TaskRecyclerAdapter(requireContext(), mTaskViewModel)
        view.plannerFragment_recycler.adapter = mRecyclerAdapter

        //observe all tasks
        mTaskViewModel.allTasks.observe(viewLifecycleOwner) {
            mTasks = it
            setDataToTaskRecyclerAdapter()
        }

        //observe all projects
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mRecyclerAdapter.setProjects(it)
        }

        //text view default state
        val textView = view.plannerFragment_textView
        textView.text = mDay.toString()

        //add task btn
        val addTaskBtn = view.plannerFragment_addTaskFab
        addTaskBtn.setOnClickListener {
            val a = PlannerFragmentDirections.actionPlannerFragmentToAddTaskFragment(null)
            findNavController().navigate(a)
        }

        //show calendar btn
        val showCalendarBtn = view.plannerFragment_showCalendarFab
        val bottomSheetBehavior = BottomSheetBehavior.from(view.plannerFragment_bottom)
        showCalendarBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        //bottom sheet calendar listener
        view.plannerFragment_bottom.bottomSheet_calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            mDay = LocalDate.of(year, month + 1, dayOfMonth)
            textView.text = mDay.toString()
            setDataToTaskRecyclerAdapter()
        }

        //bottom sheet state listener
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        addTaskBtn.isVisible = true
                        showCalendarBtn.isVisible = true
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        addTaskBtn.isVisible = true
                        showCalendarBtn.isVisible = true
                    }
                    else -> {
                        addTaskBtn.isVisible = false
                        showCalendarBtn.isVisible = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do nothing
            }
        })

        return view
    }

    /**
     * Sets data to [mRecyclerAdapter]. Filters [data] to choose only proper tasks.
     * @param data All tasks that exists
     */
    private fun setDataToTaskRecyclerAdapter() {
        mRecyclerAdapter.setData(mTasks.filter { return@filter it.date == mDay })
    }
}
