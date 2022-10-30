package com.realityexpander.tasky

import android.app.Application
import com.realityexpander.tasky.domain.AuthInfo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskyApplication: Application() {

    companion object {
        var authInfoGlobal: AuthInfo? = null
    }
}