package com.example.taskmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectViewModel
import kotlinx.android.synthetic.main.fragment_add_project.view.*

class AddProjectFragment : Fragment() {
    private lateinit var mProjectModel: ProjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_project, container, false)

        //ProjectViewModel
        mProjectModel = ViewModelProvider(this).get(ProjectViewModel::class.java)

        val btn = view.addProjectFragment_button
        val edit = view.addProjectFragment_editText
        btn.setOnClickListener {
            val txt = edit.text.toString()
            val p = Project(0, txt)

            edit.text.clear()

            findNavController().popBackStack()

            mProjectModel.addProject(p)
        }

        return view
    }
}