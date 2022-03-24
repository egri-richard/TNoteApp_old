package com.example.retrofittest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityShowTimetableBinding
import com.example.retrofittest2.logic.ShowTimetableViewModel
import com.example.retrofittest2.logic.TTElementsListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ShowTimetableActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowTimetableBinding
    private lateinit var sessionManager: SessionManager
    private val showTimetableViewModel: ShowTimetableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.btnBackFromSelectedTimetable.setOnClickListener {
            startActivity(Intent(this, TimetablesActivity::class.java))
            finish()
        }

        lifecycleScope.launchWhenCreated {
            showTimetableViewModel.getTTElements(
                sessionManager.getSelectedTimetable(),
                sessionManager.getAuthToken()
            )

            showTimetableViewModel.showTimetableUiState.collect {
                when(it) {
                    is ShowTimetableViewModel.ShowTimetableUiState.Success -> {
                        binding.lvSelectedTimetable.adapter = TTElementsListAdapter(
                            this@ShowTimetableActivity,
                            it.tteList
                        )
                    }
                    is ShowTimetableViewModel.ShowTimetableUiState.Failure -> {
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is ShowTimetableViewModel.ShowTimetableUiState.Loading -> Unit
                    is ShowTimetableViewModel.ShowTimetableUiState.Empty -> Unit
                }
            }
        }
    }
}