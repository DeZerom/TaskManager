package com.example.taskmanager.fragments.task_holders.day

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter.Companion.EMPTY_FILTERING_CONDITION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_day.view.*
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class DayFragment : Fragment() {

    private val viewModel: DayFragmentViewModel by viewModels {
        DayFragmentViewModelFactory(DatabaseController(this), activity?.application!!)
    }
    private lateinit var mTaskRecyclerAdapter: TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dbc = DatabaseController(this)

        return inflater.inflate(R.layout.fragment_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.dayFragment_recycler
        val addTaskBtn = view.dayFragment_addTaskFloatingButton
        val chooseDateBtn = view.dayFragment_calendarFloatingButton

        //listener for buttons
        addTaskBtn.setOnClickListener(viewModel.btnListener)
        chooseDateBtn.setOnClickListener(viewModel.btnListener)

        //nav events
        viewModel.navigateToAddTaskFragment.observe(viewLifecycleOwner) {
            if (it) {
                val fr = AddEditTaskFragment.addingMode()
                fr.show(parentFragmentManager, "AddEditTaskFragment_ADD_MODE")
                viewModel.navigationToAddTaskFragmentHandled()
            }
        }
        viewModel.navigateToChooseDateFragment.observe(viewLifecycleOwner) {
            if (it) {
                val f = ChooseDateFragment.chooseDate(viewModel.dayOfMonth
                    .value?.date ?: LocalDate.now())
                f.listener = viewModel.dateChangedListener
                f.show(parentFragmentManager, f.tag)
                viewModel.navigationToChooseDateFragmentHandled()
            }
        }

        //recycler adapter init
        mTaskRecyclerAdapter = TaskRecyclerAdapter(requireContext(), viewModel.databaseController,
            viewLifecycleOwner)
        mTaskRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_DAY
        mTaskRecyclerAdapter.registerCallback(object : TaskRecyclerAdapter.Callback() {
            override fun taskWantToBeEdited(task: Task) {
                val fr = AddEditTaskFragment.editingMode(task)
                fr.show(parentFragmentManager, "AddEditTaskFragment_EDIT_MODE")
            }
            override fun taskWantToChangeItsDate(task: Task) {
                val fr = ChooseDateFragment.changeDateInTask(task)
                fr.show(parentFragmentManager, fr.tag)
            }
        })
        //Filtering conditions
        mTaskRecyclerAdapter.filter.setCondition(EMPTY_FILTERING_CONDITION)
        viewModel.dayOfMonth.observe(viewLifecycleOwner) {
            mTaskRecyclerAdapter.filter
                .setCondition(it?: EMPTY_FILTERING_CONDITION)
        }

        //set recycler adapter
        recycler.adapter = mTaskRecyclerAdapter
        //set recycler layout
        recycler.layoutManager = LinearLayoutManager(requireContext())
    }
}
