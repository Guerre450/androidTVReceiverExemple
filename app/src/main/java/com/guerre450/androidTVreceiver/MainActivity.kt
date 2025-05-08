package com.guerre450.androidTVreceiver

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.cast.tv.CastReceiverContext
import com.google.android.gms.cast.tv.SenderDisconnectedEventInfo
import com.google.android.gms.cast.tv.SenderInfo
import com.guerre450.androidTVreceiver.cast.receiver.AppLifeCycleObserver
import com.guerre450.androidTVreceiver.cast.receiver.TVCustomReceiverListener
import com.guerre450.androidTVreceiver.ui.theme.AndroidTVreceiverTheme

class MainActivity : ComponentActivity() {
    var messageLisener : TVCustomReceiverListener = TVCustomReceiverListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //casting initiation
        CastReceiverContext.initInstance(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeCycleObserver())
        // linking callbacks
        CastReceiverContext.getInstance().registerEventCallback(EventCallback())
        CastReceiverContext.getInstance().setMessageReceivedListener("urn:x-cast:" + getString(R.string.sender_app), messageLisener)
        setContent {
            AndroidTVreceiverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = messageLisener.mainMessage.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private inner class EventCallback : CastReceiverContext.EventCallback() {
        override fun onSenderConnected(senderInfo: SenderInfo) {
            println("SENDER HAS BEEN CONNECTED")
            Toast.makeText(
                this@MainActivity,
                "Sender connected " + senderInfo.senderId,
                Toast.LENGTH_LONG
            )
                .show()
        }

        override fun onSenderDisconnected(eventInfo: SenderDisconnectedEventInfo) {
            println("SENDER HAS BEEN DISCONNECTED")
            Toast.makeText(
                this@MainActivity,
                "Sender disconnected " + eventInfo.senderInfo.senderId,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidTVreceiverTheme {
        Greeting("Android")
    }
}