package com.example.retrofittest2.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class NotesViewModel: ViewModel() {
    private var _notesUiState = MutableStateFlow<NotesUiState>(NotesUiState.Empty)
    val notesUiState: StateFlow<NotesUiState> = _notesUiState

    fun getNotes(id: Int, token: String) = viewModelScope.launch {
        _notesUiState.value = NotesUiState.Loading

        val response = try {
            ApiInstance.api.getNotes(id, token)
        } catch (e: IOException) {
            _notesUiState.value = NotesUiState.Failure("IOException")
            e.printStackTrace()
            return@launch
        } catch (e: HttpException) {
            _notesUiState.value = NotesUiState.Failure("HttpException")
            e.printStackTrace()
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            _notesUiState.value = NotesUiState.Success(response.body()!!)
        } else {
            _notesUiState.value = NotesUiState.Failure("Unexpected Error")
            return@launch
        }
    }

    sealed class NotesUiState {
        class Success(val notes: List<Note>): NotesUiState()
        class Failure(val message: String): NotesUiState()
        object Loading: NotesUiState()
        object Empty: NotesUiState()
    }
}