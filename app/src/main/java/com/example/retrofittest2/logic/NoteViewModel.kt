package com.example.retrofittest2.logic

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittest2.network.ApiInstance
import com.example.retrofittest2.network.models.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class NoteViewModel: ViewModel() {
    private var _noteUiState = MutableStateFlow<NoteUiState>(NoteUiState.Empty)
    val noteUiState: StateFlow<NoteUiState> = _noteUiState

    fun getNote(id: Int, token: String) = viewModelScope.launch {
        _noteUiState.value = NoteUiState.Loading

        val response = try {
          ApiInstance.api.getNote(id, token)
        } catch (e: IOException) {
            e.printStackTrace()
            _noteUiState.value = NoteUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _noteUiState.value = NoteUiState.Failure("HttpException")
            return@launch
        }

        if (response.isSuccessful && response.body() != null) {
            _noteUiState.value = NoteUiState.Loaded(response.body()!!)
        } else {
            _noteUiState.value = NoteUiState.Failure("Unexpected error")
        }
    }

    fun saveNote(id: Int, token: String, body: Note) = viewModelScope.launch {
        _noteUiState.value = NoteUiState.Loading

        Log.e("wantResponse", "saveNote: body is $body")
        val response = try {
            ApiInstance.api.updateNote(
                id,
                token,
                body)
        } catch (e: IOException) {
            e.printStackTrace()
            _noteUiState.value = NoteUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _noteUiState.value = NoteUiState.Failure("HttpException")
            return@launch
        }

        //Log.e("onResponse", "saveNote: ${response.body()}")
        if (response.isSuccessful && response.body() != null) {
            //Log.e("onResponse", "saveNote: ${response.body()}")
            _noteUiState.value = NoteUiState.Saved
        } else {
            //Log.e("onResponse", "saveNote: status code: ${response.code()}", )
            _noteUiState.value = NoteUiState.Failure("Unexpected Error")
        }
    }

    fun createNote(token: String, body: Note) = viewModelScope.launch {
        _noteUiState.value = NoteUiState.Loading

        Log.e("onCreateResponse", "createNote: $body")
        val response = try {
            Log.e("onCreateResponse", "createNote: $body")
            ApiInstance.api.newNote(token, body)
        } catch (e: IOException) {
            e.printStackTrace()
            _noteUiState.value = NoteUiState.Failure("IOException")
            return@launch
        } catch (e: HttpException) {
            e.printStackTrace()
            _noteUiState.value = NoteUiState.Failure("HttpException")
            return@launch
        }

        Log.e("onCreateResponse", "createNote: $body")
        Log.e("onCreateResponse", "createNote: ${response.body()}")
        Log.e("onCreateResponse", "createNote: ${response.code()}")

        if (response.isSuccessful && response.body() != null) {
            _noteUiState.value = NoteUiState.Saved
        } else {
            Log.e("onCreateResponse", "createNote: ${response.errorBody()!!.charStream().readText()}")
            _noteUiState.value = NoteUiState.Failure("Unexpected error")
        }
    }

    sealed class NoteUiState {
        class Loaded(val note: Note): NoteUiState()
        object Saved: NoteUiState()
        class Failure(val msg: String): NoteUiState()
        object Loading: NoteUiState()
        object Empty: NoteUiState()
    }
}