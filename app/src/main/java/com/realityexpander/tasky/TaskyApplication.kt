package com.realityexpander.tasky

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskyApplication: Application() {

    companion object {
        var savedStateHandle: SavedStateHandle = SavedStateHandle()
    }
}