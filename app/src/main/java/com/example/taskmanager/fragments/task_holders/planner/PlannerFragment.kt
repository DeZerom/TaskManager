package com.example.taskmanager.fragments.task_holders.planner

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.notifications.Notifications
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter.Companion.EMPTY_FILTERING_CONDITION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_planner.view.*
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class PlannerFragment : Fragment() {
    private val LOG_TAG = "1234"
    private val viewModel: PlannerFragmentViewModel by viewModels {
        PlannerFragmentViewModelFactory(requireActivity().application,
            DatabaseController(this))
    }
    private lateinit var taskRecyclerAdapter: TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val textView = view.plannerFragment_textView

        //recycler
        taskRecyclerAdapter = TaskRecyclerAdapter(requireContext(),
                viewModel.databaseController, viewLifecycleOwner)
        //filtering strategy and init condition
        taskRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        taskRecyclerAdapter.registerCallback(object : TaskRecyclerAdapter.Callback() {
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
        recycler.adapter = taskRecyclerAdapter

        //add task btn
        addTaskBtn.setOnClickListener(viewModel.btnListener)
        viewModel.navigateToAddTaskFragment.observe(viewLifecycleOwner) {
            if (it) {
                val fragment = AddEditTaskFragment.addingMode()
                fragment.show(parentFragmentManager, fragment.tag)
                viewModel.navigationToAddTaskFragmentHandled()
            }
        }

        //switch listener
        switch.setOnClickListener(viewModel.btnListener)

        //show calendar btn
        showCalendarBtn.setOnClickListener(viewModel.btnListener)
        viewModel.navigateToChooseDateFragment.observe(viewLifecycleOwner) {
            if (it) {
                val dayOfMonth = viewModel.dayOfMonth.value
                    ?: DayOfMonth(0, LocalDate.MIN, false)
                val f = ChooseDateFragment.chooseDate(dayOfMonth.date)
                f.callback = viewModel.dateChangedCallback
                f.show(parentFragmentManager, f.tag)
                viewModel.navigationToChooseDateFragmentHandled()
            }
        }

        //observe dayOfMonth LiveData
        viewModel.dayOfMonth.observe(viewLifecycleOwner) {day ->
            switch.isChecked = day.isWeekend
            textView.text = day.toString()
            taskRecyclerAdapter.filter.setCondition(day?: EMPTY_FILTERING_CONDITION)
        }

        //make plan button
        //TODO will be removed so I leave it here instead of moving it to the viewModel
        makePlanBtn.setOnClickListener {
            val dayOfMonth = viewModel.dayOfMonth.value ?: DayOfMonth(0, LocalDate.MIN, false)
            viewModel.databaseController.deleteMonthsExcept(dayOfMonth.date.month)
            viewModel.databaseController.deleteDuplicatedDays()
            if (!viewModel.databaseController.isMonthExists(dayOfMonth.date.month))
                viewModel.databaseController.createMonth(dayOfMonth.date.month)

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(R.string.alertDialog_recomendToAddWeekends)
            builder.setMessage(R.string.alertDialog_recomendToAddWeekends_message)
            builder.setNeutralButton(R.string.ok_string) {_, _ ->}

            builder.create().show()

            Notifications.createNotificationsForOverdueTasks(requireContext(), viewModel.databaseController)
            Notifications.createNotificationsForTodayTasks(requireContext(), viewModel.databaseController)
        }
    }
}
