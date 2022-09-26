package com.example.ipcmessenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

private const val MSG_SAY_HELLO = 1
private const val MSG_KEY = "KEY"

class MainActivity : AppCompatActivity() {

    /** Messenger for communicating with the service.  */
    private var mService: Messenger? = null

    /** Flag indicating whether we have called bind on the service.  */
    private var bound: Boolean = false

    /**
     * Class for interacting with the main interface of the service.
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            Log.d(
                "TAGG",
                "onServiceConnected() called with: className = $className, service = $service"
            )
            mService = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null
            bound = false
        }
    }

    private fun sayHello(name: String) {
        Log.d("TAGG", "sayHello() called bound = $bound, name = $name")
        if (!bound) return
        // Create and send a message to the service, using a supported 'what' value
        val bundle = Bundle()
        bundle.putString(MSG_KEY, name)
        val msg: Message = Message.obtain(null, MSG_SAY_HELLO)
        msg.data = bundle
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("TAGG", "onCreate() called with: PID = ${Process.myPid()}")
        val btnSend = findViewById<Button>(R.id.btnSend)
        val edtName = findViewById<EditText>(R.id.edt_name)
        btnSend.setOnClickListener {
            sayHello(edtName.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service
        val intent = Intent()
        // for api >= 30
        intent.`package` = "com.example.myserver"

        intent.component = ComponentName(
            "com.example.myserver",
            "com.example.myserver.MessengerService"
        )
        val isBind = bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        Log.d("TAGG", "onStart() called --- $isBind")
    }

    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (bound) {
            unbindService(mConnection)
            bound = false
        }
    }
}