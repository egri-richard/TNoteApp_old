package com.example.retrofittest2.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.Timetable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class TimetablesViewModel: ViewModel() {
    private var _timtablesUiState = MutableStateFlow<TimetablesUiState>(TimetablesUiState.Empty)
    val timetableUiState: StateFlow<TimetablesUiState> = _timtablesUiState

    fun getTimetables(token: String, id: Int) = viewModelScope.launch {
        val response = try {
            ApiInstance.api.getTimetables(id, token)
        } catch (e: IOException) {
            e.printStackTrace()
            _timtablesUiState.value = TimetablesUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _timtablesUiState.value = TimetablesUiState.Failure("HttpException")
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            _timtablesUiState.value = TimetablesUiState.Success(
                response.body()!!
            )
        } else {
            Log.e("onTTLoading", "getTimetables: ${response.body()}" )
            Log.e("onTTLoading", "getTimetables: ${response.code()}", )
            Log.e("onTTLoading", "getTimetables: ${response.errorBody()!!.charStream().readText()}")
            _timtablesUiState.value = TimetablesUiState.Failure("Unexpected error")
        }
    }

    fun newTimetable(token: String, userId: Int, name: String) = viewModelScope.launch {
        val response = try {
            ApiInstance.api.newTimetable(
                token,
                Timetable(null, userId, name)
            )
        } catch (e: IOException) {
            e.printStackTrace()
            _timtablesUiState.value = TimetablesUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _timtablesUiState.value = TimetablesUiState.Failure("HttpException")
            return@launch
        }

        Log.e("onNewTimetable", "newTimetable: ${response.body()}")
        if (response.isSuccessful && response.body() != null) {
            _timtablesUiState.value = TimetablesUiState.Created
        } else {
            _timtablesUiState.value = TimetablesUiState.Failure("Unexpected error")
        }
    }

    sealed class TimetablesUiState {
        class Success(val timetableList: List<Timetable>): TimetablesUiState()
        object Created: TimetablesUiState()
        class Failure(val message: String): TimetablesUiState()
        object Loading: TimetablesUiState()
        object Empty: TimetablesUiState()
    }
}