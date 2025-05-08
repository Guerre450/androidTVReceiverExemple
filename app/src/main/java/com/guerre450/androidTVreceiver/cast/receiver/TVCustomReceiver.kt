package com.guerre450.androidTVreceiver.cast.receiver

import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.cast.tv.CastReceiverContext

class TVCustomReceiverListener : CastReceiverContext.MessageReceivedListener {
    var mainMessage = mutableStateOf("android")

    override fun onMessageReceived(
        namespace: String, senderId: String?, message: String ) {
        mainMessage.value = message
        println(message)
    }
}