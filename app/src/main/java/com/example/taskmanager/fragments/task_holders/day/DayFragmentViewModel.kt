package com.example.taskmanager.fragments.task_holders.day

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import java.time.LocalDate

class DayFragmentViewModel(
    application: Application,
    val databaseController: DatabaseController): AndroidViewModel(application) {

    private val _dayOfMonth = MutableLiveData<DayOfMonth>()
    /**
     * Current day of month
     */
    val dayOfMonth: LiveData<DayOfMonth>
        get() = _dayOfMonth

    private val _navigateToAddTaskFragment = MutableLiveData(false)
    /**
     * Shows if we should navigate to the [AddEditTaskFragment]
     */
    val navigateToAddTaskFragment: LiveData<Boolean>
        get() = _navigateToAddTaskFragment

    private val _navigateToChooseDateFragment = MutableLiveData(false)
    /**
     * Shows if we should navigate to the [ChooseDateFragment]
     */
    val navigateToChooseDateFragment: LiveData<Boolean>
        get() = _navigateToChooseDateFragment

    /**
     * Listener for all buttons
     */
    val btnListener = View.OnClickListener {
        when(it.id) {
            R.id.dayFragment_addTaskFloatingButton -> {
                _navigateToAddTaskFragment.value = true
            }
            R.id.dayFragment_calendarFloatingButton -> {
                _navigateToChooseDateFragment.value = true
            }
        }
    }

    /**
     * Listener for [ChooseDateFragment]
     */
    val dateChangedListener = object: ChooseDateFragment.DateChangedListener {
        override fun onDateChangeListener(oldDate: LocalDate, newDate: LocalDate?) {
            newDate?.let {
                _dayOfMonth.value = databaseController.getDay(it)
            } ?: run {
                _dayOfMonth.value = null
            }
        }
    }

    fun dayOfMonthChanged(dayOfMonth: DayOfMonth) {
        _dayOfMonth.value = dayOfMonth
    }

    fun navigationToAddTaskFragmentHandled() {
        _navigateToAddTaskFragment.value = false
    }

    fun navigationToChooseDateFragmentHandled() {
        _navigateToChooseDateFragment.value = false
    }
}