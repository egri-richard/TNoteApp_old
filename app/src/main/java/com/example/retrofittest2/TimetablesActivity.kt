package com.example.retrofittest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityTimetablesBinding
import com.example.retrofittest2.logic.TimetableListAdapter
import com.example.retrofittest2.logic.TimetablesViewModel
import com.example.retrofittest2.network.models.Timetable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class TimetablesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimetablesBinding
    private val timetablesViewModel: TimetablesViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var timetables: List<Timetable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetablesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        binding.lvTimetables.isClickable = true

        binding.lvTimetables.setOnItemClickListener { adapterView, view, i, l ->
            sessionManager.saveSelectedTimetable(timetables[i].id!!)
            startActivity(Intent(this, ShowTimetableActivity::class.java))
            finish()
        }

        binding.btnBackFromTimetables.setOnClickListener {
            startActivity(Intent(this, LoggedInActivity::class.java))
            finish()
        }

        binding.btnNewTimetable.setOnClickListener {
            timetablesViewModel.newTimetable(
                sessionManager.getAuthToken(),
                sessionManager.getUserId(),
                binding.etNewTTName.text.toString()
            )
        }

        lifecycleScope.launchWhenCreated {
            timetablesViewModel.getTimetables(
                sessionManager.getAuthToken(),
                sessionManager.getUserId()
            )

            timetablesViewModel.timetableUiState.collect {
                when(it) {
                    is TimetablesViewModel.TimetablesUiState.Success -> {
                        timetables = it.timetableList
                        binding.lvTimetables.adapter = TimetableListAdapter(
                            this@TimetablesActivity,
                            it.timetableList
                        )
                        Log.e("onSuccess", "ttlist: ${it.timetableList}", )
                    }
                    is TimetablesViewModel.TimetablesUiState.Created -> {
                        timetablesViewModel.getTimetables(
                            sessionManager.getAuthToken(),
                            sessionManager.getUserId()
                        )
                    }
                    is TimetablesViewModel.TimetablesUiState.Failure -> {
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is TimetablesViewModel.TimetablesUiState.Loading -> Unit
                    is TimetablesViewModel.TimetablesUiState.Empty -> Unit
                }
            }
        }
    }
}