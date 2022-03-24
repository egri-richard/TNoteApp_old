package com.example.retrofittest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityNoteBinding
import com.example.retrofittest2.logic.NoteViewModel
import com.example.retrofittest2.logic.NotesViewModel
import com.example.retrofittest2.network.models.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var currentNote: Note
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.btnSaveShownNote.setOnClickListener {
            currentNote = sessionManager.getCurrentNote()!!
            currentNote.title = binding.etShownNoteTitle.text.toString()
            currentNote.content = binding.etShownNoteContent.text.toString()
            sessionManager.saveCurrentNote(currentNote)

            if (currentNote.id == null) {
                Log.e("onSaveNote", "onSaveNoteClick: $currentNote")
                noteViewModel.createNote(
                    sessionManager.getAuthToken(),
                    currentNote
                )
            } else {
                noteViewModel.saveNote(
                    currentNote.id!!,
                    sessionManager.getAuthToken(),
                    currentNote
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            if (sessionManager.getCurrentNote()!!.id == null) {
                binding.etShownNoteTitle.setText("")
                binding.etShownNoteContent.setText("")
            } else {
                noteViewModel.getNote(
                    sessionManager.getCurrentNote()!!.id!!,
                    sessionManager.getAuthToken()
                )
            }


            noteViewModel.noteUiState.collect {
                when(it) {
                    is NoteViewModel.NoteUiState.Loaded -> {
                        binding.etShownNoteTitle.setText(it.note.title)
                        binding.etShownNoteContent.setText(it.note.content)
                    }
                    is NoteViewModel.NoteUiState.Saved -> {
                        startActivity(Intent(this@NoteActivity, NotesActivity::class.java))
                        finish()
                    }
                    is NoteViewModel.NoteUiState.Failure -> {
                        Snackbar.make(
                            binding.root,
                            it.msg,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is NoteViewModel.NoteUiState.Loading -> Unit
                    is NoteViewModel.NoteUiState.Empty -> Unit
                }
            }
        }
    }
}