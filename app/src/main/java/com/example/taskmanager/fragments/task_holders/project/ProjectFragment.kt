package com.example.taskmanager.fragments.task_holders.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_project.view.*

class ProjectFragment : Fragment() {
    private lateinit var mProject: Project
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var mRecyclerAdapter: TaskRecyclerAdapter
    private val LOG_TAG = "1234"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get taskViewModel
        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_project, container, false)

        //set adapter to recycler
        val recyclerView = view.projectFragment_recycler
        mRecyclerAdapter = TaskRecyclerAdapter(requireContext())
        recyclerView.adapter = mRecyclerAdapter
        //set layout manager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onResume() {
        //get Project
        val tmp = arguments?.let { ProjectFragmentArgs.fromBundle(it).currentProject }
        if (tmp != null) {
            mProject = tmp
        }
        else Toast.makeText(requireContext(),
            R.string.errorToast_arguments_providing_nullError, Toast.LENGTH_LONG).show()

        //set toolbar title
        activity?.toolbar?.title = mProject.name
        //make editProject button in toolbar visible
        activity?.toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = true

        //add task button
        val btn = view?.projectFragment_floatingActionButton
        btn?.setOnClickListener {
            val action = ProjectFragmentDirections.actionProjectFragmentToAddTaskFragment(mProject)
            findNavController().navigate(action)
        }

        //observe tasks and set them to recycler
        mTaskViewModel.allTasks.observe(viewLifecycleOwner) {
            //set to recycler tasks which is owned by mProject
            mRecyclerAdapter.setData(it.filter { t ->
                return@filter t.projectOwnerId == mProject.id
            })
        }
        // TODO it's tmp solution. Should provide all projects
        mRecyclerAdapter.setProjects(listOf(mProject))

        super.onResume()
    }

    override fun onPause() {
        super.onPause()

        //hide edit project button
        activity?.toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = false
    }
}