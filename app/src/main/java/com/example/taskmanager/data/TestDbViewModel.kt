package com.example.taskmanager.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestDbViewModel(application: Application): AndroidViewModel(application) {

    private val repository = TestDbRepository(TestDB.getDb(application).testDbObjectDao())
    val data = repository.data

    fun addTestDbObj(obj: TestDbObject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTestObj(obj)
        }
    }
}