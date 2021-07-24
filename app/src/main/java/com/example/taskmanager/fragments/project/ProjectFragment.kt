package com.example.taskmanager.fragments.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_project.view.*

class ProjectFragment : Fragment() {
    private lateinit var mProject: Project
    private val LOG_TAG = "1234"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_project, container, false)

        //add task
        val btn = view.projectFragment_floatingActionButton
        btn.setOnClickListener {
            findNavController().navigate(R.id.addTaskFragment)
        }

        return view
    }

    override fun onResume() {
        Log.i(LOG_TAG, "Project fragment is resumed")
        //get Project
        val tmp = arguments?.let { ProjectFragmentArgs.fromBundle(it).currentProject }
        if (tmp != null) {
            mProject = tmp
        }
        else Toast.makeText(requireContext(),
            R.string.errorToast_arguments_providing_nullError, Toast.LENGTH_LONG).show()

        //set textView text
        val tv = view?.projectFragment_textView
        tv?.text = mProject.name
        //set toolbar title
        activity?.toolbar?.title = mProject.name
        //make editProject button in toolbar visible
        activity?.toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = true

        super.onResume()
    }

    override fun onPause() {
        Log.i(LOG_TAG, "ProjectFragment is paused")

        super.onPause()
    }

}