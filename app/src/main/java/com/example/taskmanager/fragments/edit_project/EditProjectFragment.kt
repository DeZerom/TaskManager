package com.example.taskmanager.fragments.edit_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import kotlinx.android.synthetic.main.fragment_edit_project.view.*

class EditProjectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_project, container, false)

        val btn = view.editProjectFragment_button
        btn.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}