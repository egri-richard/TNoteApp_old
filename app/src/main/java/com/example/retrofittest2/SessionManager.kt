package com.example.retrofittest2

import android.content.Context
import com.example.retrofittest2.network.models.Note
import com.google.gson.Gson

class SessionManager(context: Context) {
    private var prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val gson = Gson()

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val CURRENT_NOTE = "current_note"
        const val CURRENT_TIMETABLE = "current_timetable"
    }

    fun clearPrefs() {
        return prefs.all.clear()
    }

    fun saveCredentials(token: String, id: Int) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putInt(USER_ID, id)
        editor.apply()
    }

    fun saveCurrentNote(note: Note) {
        val editor = prefs.edit()
        editor.putString(CURRENT_NOTE, gson.toJson(note))
        editor.apply()
    }

    fun saveSelectedTimetable(id: Int) {
        val editor = prefs.edit()
        editor.putInt(CURRENT_TIMETABLE, id)
        editor.apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, 0)
    }

    fun getAuthToken(): String {
        val token = prefs.getString(USER_TOKEN, null)
        return "Bearer $token"
    }

    fun getCurrentNote(): Note? {
        val json = prefs.getString(CURRENT_NOTE, null)
        return gson.fromJson(json, Note::class.java)
    }

    fun getSelectedTimetable(): Int {
        return prefs.getInt(CURRENT_TIMETABLE, 0)
    }
}