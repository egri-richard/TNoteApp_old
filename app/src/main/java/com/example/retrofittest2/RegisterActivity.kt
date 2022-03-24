package com.example.retrofittest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.retrofittest2.databinding.ActivityRegisterBinding
import com.example.retrofittest2.logic.MainViewModel
import com.example.retrofittest2.logic.RegisterViewModel
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.RegistrationRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            registerViewModel.register(
                binding.etRegisterFullName.text.toString(),
                binding.etRegisterEmail.text.toString(),
                binding.etRegisterPassword.text.toString()
            )
        }

        lifecycleScope.launchWhenCreated {
            registerViewModel.registerUiState.collect {
                when(it) {
                    is RegisterViewModel.RegisterUiState.Success -> {
                        binding.progressBar2.isVisible = false
                        Snackbar.make(
                            binding.root,
                            "Account created",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        sessionManager.saveCredentials(it.token, it.id)
                        startActivity(Intent(this@RegisterActivity, LoggedInActivity::class.java))
                        finish()
                    }
                    is RegisterViewModel.RegisterUiState.Failure -> {
                        binding.progressBar2.isVisible = false
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is RegisterViewModel.RegisterUiState.Loading -> {
                        binding.progressBar2.isVisible = true
                    }
                    is RegisterViewModel.RegisterUiState.Empty -> Unit
                }
            }
        }
    }
}