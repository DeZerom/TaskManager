package com.example.taskmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.data.project.Project
import kotlinx.android.synthetic.main.fragment_project.view.*

class ProjectFragment : Fragment() {
    private lateinit var mProject: Project

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onResume() {
        val tmp = arguments?.let { ProjectFragmentArgs.fromBundle(it).currentProject }
        if (tmp != null) {
            mProject = tmp
            val tv = view?.projectFragment_textView
            tv?.text = mProject.name
        }
        else Toast.makeText(requireContext(),
            R.string.errorToast_arguments_providing_nullError, Toast.LENGTH_LONG).show()

        super.onResume()
    }

}