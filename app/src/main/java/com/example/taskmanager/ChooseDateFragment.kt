package com.example.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.task.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_choose_date.view.*
import java.time.LocalDate
import java.util.*

class ChooseDateFragment(date: LocalDate) : BottomSheetDialogFragment() {
    private var mDate = date

    private var mListener: DateChangedListener? = null
    var listener: DateChangedListener?
        get() = mListener
        set(value) {
            mListener = value
        }

    private var mTask: Task? = null

    private lateinit var mDatabaseController: DatabaseController

    constructor(task: Task) : this(task.date) {
        mTask = task
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabaseController = DatabaseController(this)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView = view.chooseDateFragment_calendar
        val calendar = Calendar.getInstance()   //to set date

        calendar.set(mDate.year, mDate.monthValue - 1, mDate.dayOfMonth)
        calendarView.date = calendar.timeInMillis

        //change date listener
        calendarView.setOnDateChangeListener(onDateChangeListener)
    }

    /**
     * Called when date in calendar view changed
     */
    private val onDateChangeListener =
        CalendarView.OnDateChangeListener { _, year, month, dayOfMonth ->
            val newDate = LocalDate.of(year, month + 1, dayOfMonth)
            mTask?.let {
                val task = Task.createTaskWithAnotherDate(it, newDate)
                mDatabaseController.updateTask(task)
                dismiss() // close this dialog
            } ?: run {
                listener?.onDateChangeListener(mDate, newDate)
                mDate = newDate
            }
        }

    /**
     * Callback used for indicate the user changes the date.
     */
    interface DateChangedListener {
        /**
         * Called when selected date changed and this fragment was called to change date of views
         * (PlannerFragment for example)
         * @param oldDate [LocalDate] before change
         * @param newDate [LocalDate] after change
         */
        fun onDateChangeListener(oldDate: LocalDate, newDate: LocalDate)
    }

    companion object {
        fun chooseDate(currentDate: LocalDate): ChooseDateFragment {
            return ChooseDateFragment(currentDate)
        }
        fun changeDateInTask(task: Task): ChooseDateFragment {
            return ChooseDateFragment(task)
        }
    }
}
