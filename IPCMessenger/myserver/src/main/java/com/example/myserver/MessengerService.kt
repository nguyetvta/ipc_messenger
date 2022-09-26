package com.example.myserver

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast

/** Command to the service to display a message  */
private const val MSG_SAY_HELLO = 1
private const val MSG_KEY = "KEY"

class MessengerService : Service() {

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private lateinit var mMessenger: Messenger

    /**
     * Handler of incoming messages from clients.
     */
    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.d("TAGG", "handleMessage() called with: PID = ${Process.myPid()}")
            when (msg.what) {
                MSG_SAY_HELLO ->
                    Toast.makeText(
                        applicationContext,
                        "Hello ${msg.data.getString(MSG_KEY)}",
                        Toast.LENGTH_SHORT
                    ).show()
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }
}