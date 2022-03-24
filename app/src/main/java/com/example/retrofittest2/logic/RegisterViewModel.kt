package com.example.retrofittest2.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.RegistrationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel: ViewModel() {
    private var _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Empty)
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        val response = try {
            ApiInstance.api.register(RegistrationRequest(name, email, password))
        } catch (e: IOException) {
            e.printStackTrace()
            _registerUiState.value = RegisterUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _registerUiState.value = RegisterUiState.Failure("HttpException")
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            val token = response.body()!!.token
            val id = response.body()!!.user.id
            _registerUiState.value = RegisterUiState.Success(token, id)
        } else {
            _registerUiState.value = RegisterUiState.Failure(response.errorBody().toString())
            return@launch
        }
    }
    sealed class RegisterUiState {
        class Success(val token: String, val id: Int): RegisterUiState()
        class Failure(val message: String): RegisterUiState()
        object Loading: RegisterUiState()
        object Empty: RegisterUiState()
    }
}