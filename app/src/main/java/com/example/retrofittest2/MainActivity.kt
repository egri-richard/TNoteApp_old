package com.example.retrofittest2

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityMainBinding
import com.example.retrofittest2.logic.MainViewModel
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.LoginRequest
import com.example.retrofittest2.network.models.LoginResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.btnLogin.setOnClickListener {
            mainViewModel.login(
                binding.etEmail.text.toString(),
                binding.etPass.text.toString()
            )
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        lifecycleScope.launchWhenCreated {
            mainViewModel.loginUiState.collect {
                when(it) {
                    is MainViewModel.LoginUiState.Success -> {
                        binding.progressBar.isVisible = false
                        Snackbar.make(
                            binding.root,
                            "Logged in",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        sessionManager.saveCredentials(it.token, it.id)
                        startActivity(Intent(this@MainActivity, LoggedInActivity::class.java))
                        finish()
                    }
                    is MainViewModel.LoginUiState.Failure -> {
                        binding.progressBar.isVisible = false
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is MainViewModel.LoginUiState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is MainViewModel.LoginUiState.Empty -> Unit
                }
            }
        }
    }
}