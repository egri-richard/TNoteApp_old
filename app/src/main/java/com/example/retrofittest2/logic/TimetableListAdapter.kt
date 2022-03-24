package com.example.retrofittest2.logic

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.retrofittest2.R
import com.example.retrofittest2.network.models.Timetable

class TimetableListAdapter(
    private val context: Activity,
    private val ttList: List<Timetable>
) : ArrayAdapter<Timetable>(context, R.layout.item_timetable, ttList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.item_timetable, parent, false)

        val tvTimetableName = view.findViewById<TextView>(R.id.tvTimetableName)
        val timetable = ttList[position]
        tvTimetableName.text = timetable.name

        return view
    }
}