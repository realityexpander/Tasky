package com.realityexpander.tasky

import android.app.Application
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskyApplication: Application() {

    companion object {
        var authInfoGlobal: AuthInfo? = null
    }
}