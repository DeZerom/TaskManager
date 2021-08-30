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
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.viewmodels.DayOfMonthViewModel
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_choose_date.view.*
import kotlinx.android.synthetic.main.fragment_day.view.*
import kotlinx.android.synthetic.main.fragment_planner.view.*
import java.time.LocalDate

class PlannerFragment : Fragment() {
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var mDaysViewModel: DayOfMonthViewModel
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter
    private var mTasks = emptyList<Task>()
    private var mDays = emptyList<DayOfMonth>()
    private var mCurrentDate = LocalDate.now()

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
        mDaysViewModel = provider.get(DayOfMonthViewModel::class.java)

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

        //observe all days
        mDaysViewModel.allDays.observe(viewLifecycleOwner) {
            mDays = it
        }

        //text view default state
        val textView = view.plannerFragment_textView
        textView.text = mCurrentDate.toString()

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
        val switch = view.plannerFragment_switchIsWeekend
        view.plannerFragment_bottom.bottomSheet_calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            //get current date
            mCurrentDate = LocalDate.of(year, month + 1, dayOfMonth)

            //check if this day is in mDays
            val d = mDays.find { return@find it.date == mCurrentDate }
            //if not - create it
            d?.let { switch.isChecked = it.isWeekend }
                ?: mDaysViewModel.addDay(DayOfMonth(0, mCurrentDate, switch.isChecked))

            //update views
            textView.text = mCurrentDate.toString()

            //observe tasks for the mCurrentDate
            mTaskViewModel.getTasksByDate(mCurrentDate).observe(viewLifecycleOwner) {
                setDataToTaskRecyclerAdapter(it)
            }
        }

        //switch listener
        switch.setOnCheckedChangeListener { _, isChecked ->
            //find day
            val d = mDays.find { return@find it.date == mCurrentDate }

            //if haven't found or nothing changed - return
            d ?: return@setOnCheckedChangeListener
            if (isChecked == d.isWeekend) return@setOnCheckedChangeListener

            mDaysViewModel.updateDay(DayOfMonth(d.id, d.date, isChecked))
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
     * Sets data to [mRecyclerAdapter]. Filters [mTasks] to choose only proper tasks.
     * @param data All tasks that exists
     * @see mTasks
     */
    private fun setDataToTaskRecyclerAdapter(tasks: List<Task> = mTasks) {
        mRecyclerAdapter.setData(tasks.filter { return@filter it.date == mCurrentDate })
    }
}
