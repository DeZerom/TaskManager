package com.example.taskmanager.data

import androidx.lifecycle.LiveData

class TestDbRepository(private val dao: TestDbDAO) {

    val data: LiveData<List<TestDbObject>> = dao.getAll()

    fun addTestObj(obj: TestDbObject) {
        dao.insert(obj)
    }

}