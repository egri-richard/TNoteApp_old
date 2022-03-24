package com.example.retrofittest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityNotesBinding
import com.example.retrofittest2.logic.ListAdapter
import com.example.retrofittest2.logic.NotesViewModel
import com.example.retrofittest2.network.models.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var notes: List<Note>
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        binding.lvNotes.isClickable = true

        binding.btnBackFromNotes.setOnClickListener {
            startActivity(Intent(this, LoggedInActivity::class.java))
            finish()
        }

        binding.btnNewNote.setOnClickListener {
            sessionManager.saveCurrentNote(Note(
                "",
                null,
                null,
                sessionManager.getUserId(),
                "",
                null)
            )
            startActivity(Intent(this, NoteActivity::class.java))
            finish()
        }
        
        binding.lvNotes.setOnItemClickListener { adapterView, view, i, l ->
            sessionManager.saveCurrentNote(notes[i])
            startActivity(Intent(this, NoteActivity::class.java))
            finish()
        }

        lifecycleScope.launchWhenCreated {
            notesViewModel.getNotes(
                sessionManager.getUserId(),
                sessionManager.getAuthToken()
            )

            notesViewModel.notesUiState.collect {
                when(it) {
                    is NotesViewModel.NotesUiState.Success -> {
                        notes = it.notes
                        binding.lvNotes.adapter = ListAdapter(
                            this@NotesActivity,
                            it.notes
                        )
                    }
                    is NotesViewModel.NotesUiState.Failure -> {
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is NotesViewModel.NotesUiState.Loading -> Unit
                    is NotesViewModel.NotesUiState.Empty -> Unit
                }
            }
        }
    }
}