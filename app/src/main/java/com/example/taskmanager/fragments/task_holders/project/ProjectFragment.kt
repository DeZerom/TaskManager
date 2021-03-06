package com.example.taskmanager.fragments.task_holders.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_project.view.*

class ProjectFragment : Fragment() {
    private lateinit var mProject: Project
    private lateinit var mDatabaseController: DatabaseController
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabaseController = DatabaseController(this)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.projectFragment_recycler

        //set adapter to recycler
        mRecyclerAdapter = TaskRecyclerAdapter(requireContext(), mDatabaseController,
            viewLifecycleOwner)
        mRecyclerAdapter.filteringStrategy = TaskRecyclerAdapter.FILTER_BY_PROJECT
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

        //condition will be set in onResume
        recycler.adapter = mRecyclerAdapter

        //set layout manager
        recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        //get Project
        val tmp = arguments?.let { ProjectFragmentArgs.fromBundle(it).currentProject }
        if (tmp != null) {
            mProject = tmp
        } else Toast.makeText(requireContext(),
            R.string.errorToast_arguments_providing_nullError, Toast.LENGTH_LONG).show()

        //set toolbar title
        activity?.toolbar?.title = mProject.name

        //change filtering condition
        mRecyclerAdapter.filter.setCondition(mProject)

        //add task button
        val btn = view?.projectFragment_floatingActionButton
        btn?.setOnClickListener {
            val f = AddEditTaskFragment.addingMode(mProject)
            f.show(parentFragmentManager, f.tag)
        }

        super.onResume()
    }
}
