package com.example.taskmanager.fragments.planner

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.day.DaysHandler
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
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
    private val LOG_TAG = "1234"
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var mDaysViewModel: DayOfMonthViewModel
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter
    private lateinit var mDaysHandler: DaysHandler
    private var mDays = emptyList<DayOfMonth>()
    private var mCurrentDate = LocalDate.now()
    private var mCurrentDayOfMonth = DayOfMonth(0, LocalDate.now(), false)

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

        //days handler
        mDaysHandler = DaysHandler(mDaysViewModel, viewLifecycleOwner)

        //recycler
        mRecyclerAdapter = TaskRecyclerAdapter(requireContext(), mTaskViewModel, mProjectViewModel,
            viewLifecycleOwner)
        //filtering strategy and init condition
        mRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        mRecyclerAdapter.filter.setCondition(mCurrentDayOfMonth)
        mRecyclerAdapter.registerCallback(object : TaskRecyclerAdapter.Callback() {
            override fun taskWantToBeEdited(task: Task) {
                val f = AddEditTaskFragment.editingMode(task)
                f.show(parentFragmentManager, f.tag)
            }
        })
        view.plannerFragment_recycler.adapter = mRecyclerAdapter

        //observe all days
        val textView = view.plannerFragment_textView
        mDaysViewModel.allDays.observe(viewLifecycleOwner) {
            mDays = it
            textView.text = mCurrentDayOfMonth.toString()
        }

        //text view default state
        textView.text = mCurrentDayOfMonth.toString()

        //add task btn
        val addTaskBtn = view.plannerFragment_addTaskFab
        addTaskBtn.setOnClickListener {
            val f = AddEditTaskFragment.addingMode()
            f.show(parentFragmentManager, f.tag)
        }

        //show calendar btn
        val showCalendarBtn = view.plannerFragment_showCalendarFab
        val calendarBottomSheet = view.plannerFragment_bottom
        val calendarBottomSheetBehavior = BottomSheetBehavior.from(calendarBottomSheet)
        showCalendarBtn.setOnClickListener {
            calendarBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        //bottom sheets state listener
        calendarBottomSheetBehavior.addBottomSheetCallback(bottomsSheetsCallback)

        //choose date bottom sheet calendar listener
        val switch = view.plannerFragment_switchIsWeekend
        view.plannerFragment_bottom.bottomSheet_calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            //get current date
            mCurrentDate = LocalDate.of(year, month + 1, dayOfMonth)

            //check if this day is in mDays
            val d = mDays.find { return@find it.date == mCurrentDate }
            //if not - create it
            d?.let {
                mCurrentDayOfMonth = it
            } ?: run {
                mCurrentDayOfMonth = DayOfMonth(0, mCurrentDate, false)
                mDaysViewModel.addDay(mCurrentDayOfMonth)
            }

            //change switch state
            switch.isChecked = mCurrentDayOfMonth.isWeekend

            //update views
            textView.text = mCurrentDayOfMonth.toString()

            //change filtering condition
            mRecyclerAdapter.filter.setCondition(mCurrentDayOfMonth)
        }

        //switch listener
        switch.setOnCheckedChangeListener { _, isChecked ->
            //find day
            val d = mDays.find { return@find it.date == mCurrentDate }

            //if haven't found or nothing changed - return
            d ?: return@setOnCheckedChangeListener
            if (isChecked == d.isWeekend) return@setOnCheckedChangeListener

            mCurrentDayOfMonth = DayOfMonth(d.id, d.date, isChecked)
            mDaysViewModel.updateDay(mCurrentDayOfMonth)
        }

        //make plan button
        val makePlanBtn = view.plannerFragment_makePlanBtn
        makePlanBtn.setOnClickListener {
            mDaysHandler.deleteExcept()
            mDaysHandler.deleteDuplicates()
            if (!mDaysHandler.isMonthExists()) mDaysHandler.createMonth()

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Add holidays")
            builder.setMessage("It is recommended to add holidays by switching 'weekend'")
            builder.setNeutralButton("Ok") {_, _ ->}

            builder.create().show()
        }

        return view
    }

    /**
     * [BottomSheetBehavior.BottomSheetCallback] for bottom sheets
     */
    private val bottomsSheetsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            val addTaskBtn = view?.plannerFragment_addTaskFab
            val showCalendarBtn = view?.plannerFragment_showCalendarFab

            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    addTaskBtn?.isVisible = true
                    showCalendarBtn?.isVisible = true
                }
                BottomSheetBehavior.STATE_HIDDEN -> {
                    addTaskBtn?.isVisible = true
                    showCalendarBtn?.isVisible = true
                }
                else -> {
                    addTaskBtn?.isVisible = false
                    showCalendarBtn?.isVisible = false
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            //do nothing
        }
    }
}
