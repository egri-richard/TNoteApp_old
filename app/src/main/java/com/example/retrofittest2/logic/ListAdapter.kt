package com.example.retrofittest2.logic

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.retrofittest2.R
import com.example.retrofittest2.network.models.Note

class ListAdapter(
    private val context: Activity,
    private val notesList: List<Note>
) : ArrayAdapter<Note>(context, R.layout.item_note, notesList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.item_note, parent, false)

        val tvTitle = view.findViewById<TextView>(R.id.tvNoteTitle)
        val note = notesList[position]
        tvTitle.text = "id:${note.id} ${note.title}"

        return view
    }
}