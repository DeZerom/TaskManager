package com.example.taskmanager.fragments.home

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.R
import com.example.taskmanager.data.repositories.ProjectRepository

class HomeFragmentViewModel(application: Application): AndroidViewModel(application) {

    private val projectRepository: ProjectRepository = ProjectRepository(application)
    private val _navigateToAddProjectFragment = MutableLiveData(false)

    /**
     * Contains boolean that tells if we should navigate to [ProjectFragment]
     */
    val navigateToProjectFragment: LiveData<Boolean>
        get() = _navigateToAddProjectFragment

    /**
     * Contains all [Project]s
     */
    val allProjects = projectRepository.allProjects

    /**
     * Listener for [R.id.homeFragment_floatingActionButton]
     */
    val btnListener = View.OnClickListener {
        Log.i("HomeFragmentViewModel", "listener invoked")
        when(it.id) {
            R.id.homeFragment_floatingActionButton -> {
                Log.i("HomeFragmentViewModel", "floating button id here")
                _navigateToAddProjectFragment.value = true }
        }
    }

    /**
     * Should be called after navigation to [ProjectFragment] is handled. Changes the
     * [navigateToProjectFragment] value.
     */
    fun navigationToProjectFragmentHandled() {
        _navigateToAddProjectFragment.value = false
    }
}