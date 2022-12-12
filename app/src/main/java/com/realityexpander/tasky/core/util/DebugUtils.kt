package com.realityexpander.tasky.core.util

import android.content.Intent
import logcat.logcat

//Bundle bundle = intent.getExtras();

fun Intent.dumpIntentExtras() {
    val bundle = this.extras
    bundle ?: return

    logcat {"┌-Intent Extras:" }
    for (key in bundle.keySet()!!) {
        logcat { "┡->$key : " + if(bundle.get(key) != null) bundle.get(key) else "NULL" }
    }
}