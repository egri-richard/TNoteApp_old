package com.example.retrofittest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityLoggedInBinding
import com.example.retrofittest2.logic.LoggedInViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class LoggedInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoggedInBinding
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.btnShowTimetables.setOnClickListener {
            startActivity(Intent(this, TimetablesActivity::class.java))
            finish()
        }

        binding.btnShowNotes.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
            finish()
        }

        binding.btnShowId.setOnClickListener {
            Toast.makeText(applicationContext, sessionManager.getUserId().toString(), Toast.LENGTH_SHORT).show()
        }

        binding.btnShowToken.setOnClickListener {
            Toast.makeText(applicationContext, sessionManager.getAuthToken(), Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            loggedInViewModel.logout(sessionManager.getAuthToken())
        }

        lifecycleScope.launchWhenCreated {
            loggedInViewModel.loggedInUiState.collect {
                when(it) {
                    is LoggedInViewModel.LoggedInUiState.Success -> {
                        binding.progressBar3.isVisible = true
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@LoggedInActivity, MainActivity::class.java))
                        finish()
                    }
                    is LoggedInViewModel.LoggedInUiState.Failure -> {
                        binding.progressBar3.isVisible = true
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is LoggedInViewModel.LoggedInUiState.Loading -> {
                        binding.progressBar3.isVisible = true
                    }
                    is LoggedInViewModel.LoggedInUiState.Empty -> Unit
                }
            }
        }
    }
}