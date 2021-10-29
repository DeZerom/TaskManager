package com.example.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_choose_date.view.*
import java.time.LocalDate
import java.util.*

class ChooseDateFragment(date: LocalDate) : BottomSheetDialogFragment() {
    private var mDate = date


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView = view.chooseDateFragment_calendar
        val calendar = Calendar.getInstance()   //to set date

        calendar.set(mDate.year, mDate.monthValue - 1, mDate.dayOfMonth)
        calendarView.date = calendar.timeInMillis

        //change date listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            mDate = LocalDate.of(year, month + 1, dayOfMonth)
        }
    }
}