package com.example.taskmanager.fragments.task_holders.planner

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskmanager.notifications.Notifications
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import kotlinx.android.synthetic.main.bottom_choose_date.view.*
import kotlinx.android.synthetic.main.fragment_day.view.*
import kotlinx.android.synthetic.main.fragment_planner.*
import kotlinx.android.synthetic.main.fragment_planner.view.*
import java.time.LocalDate

class PlannerFragment : Fragment() {
    private val LOG_TAG = "1234"
    private lateinit var mDatabaseController: DatabaseController
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter

//    private var mCurrentDate = LocalDate.now()
//        set(value) {
//            field = value
//            if (mDatabaseController.isDaysLoaded)
//                mDayOfMonth = mDatabaseController.getDay(field)
//        }

    private var mDayOfMonth = DayOfMonth(0, LocalDate.now(), false)
        set(value) {
            field = value
            view?.plannerFragment_switchIsWeekend?.isChecked = field.isWeekend
            view?.plannerFragment_textView?.text = field.toString()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabaseController = DatabaseController(this)
        //initialize mCurrentDayOfMonth
        mDatabaseController.whenDaysLoaded = {
            mDayOfMonth = mDatabaseController.getDay(LocalDate.now())
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.plannerFragment_recycler
        val addTaskBtn = view.plannerFragment_addTaskFab
        val switch = view.plannerFragment_switchIsWeekend
        val makePlanBtn = view.plannerFragment_makePlanBtn
        val showCalendarBtn = view.plannerFragment_showCalendarFab

        //recycler
        mRecyclerAdapter = TaskRecyclerAdapter(requireContext(),
                mDatabaseController, viewLifecycleOwner)
        //filtering strategy and init condition
        mRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        mRecyclerAdapter.filter.setCondition(mDayOfMonth)
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
        //set adapter
        recycler.adapter = mRecyclerAdapter

        //add task btn
        addTaskBtn.setOnClickListener {
            val f = AddEditTaskFragment.addingMode()
            f.show(parentFragmentManager, f.tag)
        }

        //switch listener
        switch.setOnCheckedChangeListener { _, isChecked ->
            //if nothing changed - return
            if (isChecked == mDayOfMonth.isWeekend) return@setOnCheckedChangeListener

            mDayOfMonth = DayOfMonth
                .createWithAnotherIsWeekend(mDayOfMonth, isChecked)
            mDatabaseController.updateDay(mDayOfMonth)
        }

        //show calendar btn
        showCalendarBtn.setOnClickListener {
            val f = ChooseDateFragment.chooseDate(mDayOfMonth.date)
            f.listener = dataChangedListener
            f.show(parentFragmentManager, f.tag)
        }

        //make plan button
        makePlanBtn.setOnClickListener {
            mDatabaseController.deleteMonthsExcept(mDayOfMonth.date.month)
            mDatabaseController.deleteDuplicatedDays()
            if (!mDatabaseController.isMonthExists(mDayOfMonth.date.month))
                mDatabaseController.createMonth(mDayOfMonth.date.month)

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(R.string.alertDialog_recomendToAddWeekends)
            builder.setMessage(R.string.alertDialog_recomendToAddWeekends_message)
            builder.setNeutralButton(R.string.ok_string) {_, _ ->}

            builder.create().show()

            Notifications.createNotificationsForOverdueTasks(requireContext(), mDatabaseController)
            Notifications.createNotificationsForTodayTasks(requireContext(), mDatabaseController)
        }
    }

    private val dataChangedListener = object : ChooseDateFragment.DateChangedListener {
        override fun onDateChangeListener(oldDate: LocalDate, newDate: LocalDate?) {
            newDate?.let {
                mDayOfMonth = mDatabaseController.getDay(it)
            }
        }
    }
}
