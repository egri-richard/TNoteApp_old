package com.example.retrofittest2.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.TTElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ShowTimetableViewModel: ViewModel() {
    private var _showTimetableUiState = MutableStateFlow<ShowTimetableUiState>(ShowTimetableUiState.Empty)
    val showTimetableUiState: StateFlow<ShowTimetableUiState> = _showTimetableUiState

    fun getTTElements(id: Int, token: String) = viewModelScope.launch {
        _showTimetableUiState.value = ShowTimetableUiState.Loading
        val response = try {
            ApiInstance.api.showTimetable(id, token)
        } catch (e: IOException) {
            e.printStackTrace()
            _showTimetableUiState.value = ShowTimetableUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _showTimetableUiState.value = ShowTimetableUiState.Failure("HttpException")
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            _showTimetableUiState.value = ShowTimetableUiState.Success(
                response.body()!!
            )
        } else {
            _showTimetableUiState.value = ShowTimetableUiState.Failure("Unexpected Error")
        }
    }

    sealed class ShowTimetableUiState() {
        class Success(val tteList: List<TTElement>): ShowTimetableUiState()
        class Failure(val message: String): ShowTimetableUiState()
        object Loading: ShowTimetableUiState()
        object Empty: ShowTimetableUiState()
    }

}