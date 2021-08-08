package com.example.taskmanager.fragments.task_holders.day

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.fragments.home.ProjectRecyclerAdapter
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_day.view.*
import java.time.LocalDate

class DayFragment : Fragment() {
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mDay: LocalDate
    private lateinit var mTaskRecyclerAdapter: TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_day, container, false)

        //view models
        val provider = ViewModelProvider(this)
        mProjectViewModel = provider.get(ProjectViewModel::class.java)
        mTaskViewModel = provider.get(TaskViewModel::class.java)

        //recycler adapter init
        mTaskRecyclerAdapter = TaskRecyclerAdapter(requireContext())
        //set recycler adapter
        view.dayFragment_recycler.adapter = mTaskRecyclerAdapter
        //set recycler layout
        view.dayFragment_recycler.layoutManager = LinearLayoutManager(requireContext())

        //observe projects to put them into task_row's spinner
        mProjectViewModel.allProjects.observe(viewLifecycleOwner) {
            mTaskRecyclerAdapter.setProjects(it)
        }

        //addTask btn
        //addTaskFragment asks for a Project to set default parent project for task in spinner view,
        //so we can provide null. AddTaskFragment will handle it
        val addTaskBtn = view.dayFragment_floatingActionButton
        addTaskBtn.setOnClickListener {
            val a = DayFragmentDirections.actionDayFragmentToAddTaskFragment(null)
            findNavController().navigate(a)
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        //get mDay from arguments
        //TODO tmp solution
        mDay = LocalDate.now()

        //observe tasks to take today's ones only
        mTaskViewModel.allTasks.observe(viewLifecycleOwner) {
            mTaskRecyclerAdapter.setData(it.filter { t ->
                return@filter t.date == mDay
            })
        }
    }
}