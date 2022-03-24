package com.example.retrofittest2.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainViewModel: ViewModel() {
    private var _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState


    fun login(name: String, password: String) = viewModelScope.launch {
        _loginUiState.value = LoginUiState.Loading
        val response = try {
            ApiInstance.api.login(LoginRequest(name, password))
        } catch (e: IOException) {
            e.printStackTrace()
            _loginUiState.value = LoginUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _loginUiState.value = LoginUiState.Failure("HttpException")
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            val token = response.body()!!.token
            val id = response.body()!!.user.id
            _loginUiState.value = LoginUiState.Success(token, id)
        } else {
            Log.e("RetrofitError", response.errorBody().toString() )
            _loginUiState.value = LoginUiState.Failure(response.errorBody().toString())
        }
    }

    sealed class LoginUiState {
        data class Success(val token: String, val id: Int): LoginUiState()
        data class Failure(val message: String): LoginUiState()
        object Loading: LoginUiState()
        object Empty: LoginUiState()
    }

}