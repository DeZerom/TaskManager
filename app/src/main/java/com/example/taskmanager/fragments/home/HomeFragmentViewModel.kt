package com.example.taskmanager.fragments.home

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.repositories.ProjectRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel(application: Application): AndroidViewModel(application) {

    private val projectRepository: ProjectRepository = ProjectRepository(application)
    private val _navigateToProjectFragment = MutableLiveData(false)

    init {
        viewModelScope.launch {
            projectRepository.getAllProjects()
        }
    }

    /**
     * Contains boolean that tells if we should navigate to [ProjectFragment]
     */
    val navigateToProjectFragment: LiveData<Boolean>
        get() = _navigateToProjectFragment

    /**
     * Contains all [Project]s
     */
    val allProjects = projectRepository.allProjects

    /**
     * Listener for [R.id.homeFragment_floatingActionButton]
     */
    val addBtnListener = View.OnClickListener {
        _navigateToProjectFragment.value = true
    }

    /**
     * Should be called after navigation to [ProjectFragment] is handled. Changes the
     * [navigateToProjectFragment] value.
     */
    fun navigationToProjectFragmentHandled() {
        _navigateToProjectFragment.value = true
    }
}