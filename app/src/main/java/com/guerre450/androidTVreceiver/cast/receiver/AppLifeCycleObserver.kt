package com.guerre450.androidTVreceiver.cast.receiver

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.cast.tv.CastReceiverContext

class AppLifeCycleObserver : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        // App prepares to enter foreground.
        println("STARTED CASTING")
        CastReceiverContext.getInstance().start()
    }

    override fun onStop(owner: LifecycleOwner) {
        // App has moved to the background or has terminated.
        println("STOPPED CASTING")
        CastReceiverContext.getInstance().stop()
    }
}