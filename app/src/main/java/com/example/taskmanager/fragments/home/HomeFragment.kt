package com.example.taskmanager.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.fragments.project.RecyclerAdapter
import com.example.taskmanager.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.home_fragment.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        //add project button
        val addBtn = view.homeFragment_floatingActionButton
        addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProjectFragment)
        }

        return view
    }
}