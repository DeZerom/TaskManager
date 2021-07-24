package com.example.taskmanager.fragments.edit_project

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.viewmodels.ProjectViewModel
import kotlinx.android.synthetic.main.activity_main.*
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

        //setting field from arguments bundle
        val edit = view.editProjectFragment_editText
        edit.setText(args.currentItem.name)

        //apply button listener logic
        val applyBtn = view.editProjectFragment_button
        applyBtn.setOnClickListener {
            val project = Project(args.currentItem.id, edit.text.toString())
            mProjectModel.updateProject(project)
            val a = EditProjectFragmentDirections.actionGlobalProjectFragment(project)
            findNavController().navigate(a)
        }

        //delete button listener logic
        val deleteBtn = view.editProjectFragment_delete_button
        deleteBtn.setOnClickListener {
            //alert builder
            val builder = AlertDialog.Builder(requireContext())

            //title
            var tmp = getString(R.string.deleting_alert_title)
            tmp += " ${args.currentItem.name}?"
            builder.setTitle(tmp)

            //message
            tmp = getString(R.string.deleting_alert_message)
            tmp += " ${args.currentItem.name}?"
            builder.setMessage(tmp)

            //buttons
            builder.setPositiveButton(R.string.deleting_alert_pos_btn) {_, _ ->
                mProjectModel.deleteProject(args.currentItem)
                findNavController().navigate(R.id.homeFragment)
            }
            builder.setNegativeButton(R.string.deleting_alert_neg_btn) {_, _ ->}

            builder.create().show()
        }

        return view
    }

    override fun onResume() {
        activity?.toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = false

        super.onResume()
    }

    override fun onPause() {
        activity?.toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = true

        super.onPause()
    }
}