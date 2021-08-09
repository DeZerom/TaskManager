package com.example.taskmanager.fragments.task_holders.month

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskmanager.R
import kotlinx.android.synthetic.main.fragment_month.view.*
import java.time.LocalDate

class MonthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_month, container, false)

        val calendar = view.monthFragment_calendar
        val text = view.textView
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = LocalDate.of(year, month, dayOfMonth)
            LocalDate.of(year, month, dayOfMonth)
            text.text = date.toString()
        }

        return view
    }
}