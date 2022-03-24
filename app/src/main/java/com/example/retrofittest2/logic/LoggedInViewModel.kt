package com.example.retrofittest2.logic

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.MainActivity
import com.example.retrofittest2.network.ApiInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoggedInViewModel: ViewModel() {
    private var _loggedInUiState = MutableStateFlow<LoggedInUiState>(LoggedInUiState.Empty)
    val loggedInUiState: StateFlow<LoggedInUiState> = _loggedInUiState

    fun logout(token: String) = viewModelScope.launch {
        _loggedInUiState.value = LoggedInUiState.Loading
        val response = try {
            ApiInstance.api.logout(token)
        } catch (e: IOException) {
            e.printStackTrace()
            _loggedInUiState.value = LoggedInUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _loggedInUiState.value = LoggedInUiState.Failure("HttpException")
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            _loggedInUiState.value = LoggedInUiState.Success(response.body()!!.message)
        } else {
            _loggedInUiState.value = LoggedInUiState.Failure(response.errorBody().toString())
        }
    }

    sealed class LoggedInUiState {
        class Success(val message: String): LoggedInUiState()
        class Failure(val message: String): LoggedInUiState()
        object Loading: LoggedInUiState()
        object Empty: LoggedInUiState()
    }
}