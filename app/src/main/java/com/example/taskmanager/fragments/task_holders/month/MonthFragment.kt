package com.example.taskmanager.fragments.task_holders.month

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskmanager.R
import kotlinx.android.synthetic.main.bottom_choose_date.view.*
import kotlinx.android.synthetic.main.fragment_month.view.*
import java.time.LocalDate

class MonthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_month, container, false)

        var date = LocalDate.now()

        val bottom = view.monthFragment_bottomView
        val calendar = bottom.bottomSheet_calendar
        val te = view.monthFragment_textView
        te.text = date.toString()

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            date = LocalDate.of(year, month + 1, dayOfMonth)
            te.text = date.toString()
        }

        return view
    }
}