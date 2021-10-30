package com.example.taskmanager.fragments.task_holders.planner

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_choose_date.view.*
import kotlinx.android.synthetic.main.fragment_day.view.*
import kotlinx.android.synthetic.main.fragment_planner.*
import kotlinx.android.synthetic.main.fragment_planner.view.*
import java.time.LocalDate

class PlannerFragment : Fragment() {
    private val LOG_TAG = "1234"
    private lateinit var mDatabaseController: DatabaseController
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter
    private var mCurrentDate = LocalDate.now()
    private var mCurrentDayOfMonth = DayOfMonth(0, mCurrentDate, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_planner, container, false)

        mDatabaseController = DatabaseController(this)

        //recycler
        mRecyclerAdapter = TaskRecyclerAdapter(requireContext(), mDatabaseController, viewLifecycleOwner)
        //filtering strategy and init condition
        mRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        mRecyclerAdapter.filter.setCondition(mCurrentDayOfMonth)
        mRecyclerAdapter.registerCallback(object : TaskRecyclerAdapter.Callback() {
            override fun taskWantToBeEdited(task: Task) {
                val f = AddEditTaskFragment.editingMode(task)
                f.show(parentFragmentManager, f.tag)
            }
            override fun taskWantToChangeItsDate(task: Task) {
                val f = ChooseDateFragment.changeDateInTask(task)
                f.show(parentFragmentManager, f.tag)
            }
        })
        view.plannerFragment_recycler.adapter = mRecyclerAdapter

        //observe all days
        val textView = view.plannerFragment_textView
        mDatabaseController.daysViewModel.allDays.observe(viewLifecycleOwner) {
            mCurrentDayOfMonth = mDatabaseController.getDay(mCurrentDate)
            textView.text = mCurrentDayOfMonth.toString()
        }

        //add task btn
        val addTaskBtn = view.plannerFragment_addTaskFab
        addTaskBtn.setOnClickListener {
            val f = AddEditTaskFragment.addingMode()
            f.show(parentFragmentManager, f.tag)
        }

        //show calendar btn
        val showCalendarBtn = view.plannerFragment_showCalendarFab
        val switch = view.plannerFragment_switchIsWeekend
        showCalendarBtn.setOnClickListener {
            val f = ChooseDateFragment(mCurrentDate)
            f.listener = object : ChooseDateFragment.DateChangedListener {
                override fun onDateChangeListener(oldDate: LocalDate, newDate: LocalDate) {
                    mCurrentDate = newDate
                    mCurrentDayOfMonth = mDatabaseController.getDay(mCurrentDate)

                    mRecyclerAdapter.filter.setCondition(mCurrentDayOfMonth)

                    switch.isChecked = mCurrentDayOfMonth.isWeekend
                    textView.text = mCurrentDayOfMonth.toString()
                }
            }
            f.show(parentFragmentManager, f.tag)
        }



        //switch listener
        switch.setOnCheckedChangeListener { _, isChecked ->
            //if nothing changed - return
            if (isChecked == mCurrentDayOfMonth.isWeekend) return@setOnCheckedChangeListener

            Log.i("1234", "$isChecked ${mCurrentDayOfMonth.date} ${mCurrentDayOfMonth.isWeekend}")
            mCurrentDayOfMonth = DayOfMonth.createWithAnotherIsWeekend(mCurrentDayOfMonth, isChecked)
            mDatabaseController.updateDay(mCurrentDayOfMonth)
        }

        //make plan button
        val makePlanBtn = view.plannerFragment_makePlanBtn
        makePlanBtn.setOnClickListener {
            mDatabaseController.deleteMonthsExcept(mCurrentDate.month)
            mDatabaseController.deleteDuplicatedDays()
            if (!mDatabaseController.isMonthExists(mCurrentDate.month))
                mDatabaseController.createMonth(mCurrentDate.month)

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(R.string.alertDialog_recomendToAddWeekends)
            builder.setMessage(R.string.alertDialog_recomendToAddWeekends_message)
            builder.setNeutralButton(R.string.ok_string) {_, _ ->}

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
