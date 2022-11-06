package com.realityexpander.tasky

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskyApplication: Application() {

    companion object {
        // Workaround until figure out why `Compose-destinations` is not passing in the SavedStateHandle
        var savedStateHandle: SavedStateHandle = SavedStateHandle()
    }
}