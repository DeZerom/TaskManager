package com.example.taskmanager.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.view.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        //add project button
        val addBtn = view.homeFragment_floatingActionButton
        addBtn.setOnClickListener(viewModel.btnListener)

        viewModel.navigateToProjectFragment.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigate(R.id.action_homeFragment_to_addProjectFragment)
                viewModel.navigationToProjectFragmentHandled()
            }
        }

        //recycler
        val r = view.homeFragment_recycler
        val adapter = ProjectRecyclerAdapter()
        r.adapter = adapter
        r.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allProjects.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        return view
    }
}