package com.example.openinapp.Data

import android.app.Application
import android.content.Context

class AppDelegate : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")
        editor.apply()
    }

}