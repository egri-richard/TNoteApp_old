package com.example.retrofittest2.logic

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.retrofittest2.R
import com.example.retrofittest2.network.models.TTElement
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.*

class TTElementsListAdapter(
    private val context: Activity,
    private val list: List<TTElement>
) : ArrayAdapter<TTElement>(context, R.layout.item_ttelement) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.item_ttelement, parent, false)

        val tvLessonTitle = view.findViewById<TextView>(R.id.tvLessonTitle)
        val tvLessonStart = view.findViewById<TextView>(R.id.tvLessonStart)
        val tvLessonEnd = view.findViewById<TextView>(R.id.tvLessonEnd)

        tvLessonTitle.text = list[position].title
        tvLessonStart.text = "${list[position].start}-tól"
        tvLessonEnd.text = "${list[position].end}-ig"

        return view
    }
}