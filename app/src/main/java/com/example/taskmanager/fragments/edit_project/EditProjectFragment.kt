package com.example.taskmanager.fragments.edit_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectViewModel
import kotlinx.android.synthetic.main.fragment_edit_project.view.*

class EditProjectFragment : Fragment() {

    private lateinit var args: EditProjectFragmentArgs
    private lateinit var mProjectModel: ProjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_project, container, false)
        //lateinits
        arguments?.let { args = EditProjectFragmentArgs.fromBundle(it) }
        mProjectModel = ViewModelProvider(this).get(ProjectViewModel::class.java)

        val edit = view.editProjectFragment_editText
        edit.setText(args.currentItem.name)

        val btn = view.editProjectFragment_button
        btn.setOnClickListener {
            val project = Project(args.currentItem.id, edit.text.toString())
            mProjectModel.updateProject(project)
            findNavController().popBackStack()
        }

        return view
    }
}