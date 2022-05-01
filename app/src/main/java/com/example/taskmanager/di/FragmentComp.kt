package com.example.taskmanager.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.fragments.task_holders.AddEditTaskFragment
import com.example.taskmanager.fragments.task_holders.ChooseDateFragment
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object FragmentScopedModule {
    @Provides
    fun provideTaskRecyclerAdapterDefaultCallback(fragment: Fragment): TaskRecyclerAdapter.Callback {
        return object : TaskRecyclerAdapter.Callback() {
            private val parentFragmentManager = fragment.parentFragmentManager

            override fun taskWantToBeEdited(task: Task) {
                val f = AddEditTaskFragment.editingMode(task)
                f.show(parentFragmentManager, f.tag)
            }

            override fun taskWantToChangeItsDate(task: Task) {
                val f = ChooseDateFragment.changeDateInTask(task)
                f.show(parentFragmentManager, f.tag)
            }
        }
    }

    @Provides
    fun provideFragmentLifecycleOwner(fragment: Fragment): LifecycleOwner {
        return fragment.viewLifecycleOwner
    }
}