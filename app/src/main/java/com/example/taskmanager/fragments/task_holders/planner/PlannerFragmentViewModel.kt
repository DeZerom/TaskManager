package com.example.taskmanager.fragments.task_holders.planner

import android.app.Application
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter.Companion.EMPTY_FILTERING_CONDITION
import kotlinx.android.synthetic.main.fragment_planner.view.*
import java.time.LocalDate

class PlannerFragmentViewModel(
    application: Application,

    /**
     * [DatabaseController] instance for this fragment
     */
    val databaseController: DatabaseController
): AndroidViewModel(application) {
    private val _dayOfMonth = MutableLiveData<DayOfMonth>()
    private val _navigateToAddTaskFragment = MutableLiveData(false)
    private val _navigateToChooseDateFragment = MutableLiveData(false)

    /**
     * Current selected day of month
     */
    val dayOfMonth: LiveData<DayOfMonth>
        get() = _dayOfMonth

    /**
     * Shows if we have to navigate to the [AddEditTaskFragment]
     */
    val navigateToAddTaskFragment: LiveData<Boolean>
        get() = _navigateToAddTaskFragment

    /**
     * Shows if we have to navigate to the [ChooseDateFragment]
     */
    val navigateToChooseDateFragment: LiveData<Boolean>
        get() = _navigateToChooseDateFragment

    /**
     * Listener for all buttons
     */
    val btnListener = View.OnClickListener {
        when (it.id) {
            R.id.plannerFragment_addTaskFab -> {
                _navigateToAddTaskFragment.value = true
            }
            R.id.plannerFragment_switchIsWeekend -> {
                //if nothing changed - return
                var dayOfMonth = dayOfMonth.value ?: DayOfMonth(0, LocalDate.MIN, false)
                it as CompoundButton
                if (it.isChecked == dayOfMonth.isWeekend) return@OnClickListener

                dayOfMonth = dayOfMonth.copy(isWeekend = it.isChecked)
                databaseController.updateDay(dayOfMonth)
                changeDayOfMonth(dayOfMonth.date)
            }
            R.id.plannerFragment_showCalendarFab -> {
                _navigateToChooseDateFragment.value = true
            }
        }
    }

    /**
     * Callback for [ChooseDateFragment.DateChangedCallback]
     */
    val dateChangedCallback = object : ChooseDateFragment.DateChangedCallback {
        override fun onDateChangeListener(oldDate: LocalDate, newDate: LocalDate?) {
            newDate?.let {
                changeDayOfMonth(it)
            }
        }
    }

    init {
        databaseController.whenDaysLoaded = {
            changeDayOfMonth(LocalDate.now())
        }
    }

    /**
     * Changes the value of a current selected day of month. [dayOfMonth]'s value will be changed
     */
    fun changeDayOfMonth(date: LocalDate) {
        _dayOfMonth.value = databaseController.getDay(date)
    }

    /**
     * Should be called right after showing [AddEditTaskFragment]. Changes the value of a
     * [navigateToAddTaskFragment]
     */
    fun navigationToAddTaskFragmentHandled() {
        _navigateToAddTaskFragment.value = false
    }

    /**
     * Should be called right after navigation to [ChooseDateFragment]. Changes the value of a
     * [navigateToChooseDateFragment]
     */
    fun navigationToChooseDateFragmentHandled() {
        _navigateToChooseDateFragment.value = false
    }
}