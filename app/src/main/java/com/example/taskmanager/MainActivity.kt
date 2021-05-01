package com.example.taskmanager

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tb = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tb)

        tb.title = getString(R.string.app_name)
    }
}