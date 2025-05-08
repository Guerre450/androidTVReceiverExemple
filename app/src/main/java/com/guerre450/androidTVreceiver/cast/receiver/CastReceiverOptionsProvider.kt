package com.guerre450.androidTVreceiver.cast.receiver

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.google.android.gms.cast.tv.CastReceiverOptions
import com.google.android.gms.cast.tv.ReceiverOptionsProvider
import com.guerre450.androidTVreceiver.R


class CastReceiverOptionsProvider : ReceiverOptionsProvider {
    override fun getOptions(context: Context): CastReceiverOptions {
        val namespaces : List<String> = listOf("urn:x-cast:" + getString(context, R.string.sender_app))
        return CastReceiverOptions.Builder(context)
            .setStatusText("Cast Connect Codelab")
            .setCustomNamespaces(namespaces)
            .build()
    }
}
