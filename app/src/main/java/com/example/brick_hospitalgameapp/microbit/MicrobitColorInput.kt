package com.example.brick_hospitalgameapp.microbit

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.*

class MicrobitColorInput(private val context: Context) {

    private var usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var usbPort: UsbSerialPort? = null

    var colorState by mutableStateOf<Color?>(null)
        private set

    companion object {
        private const val ACTION_USB_PERMISSION = "com.example.USB_PERMISSION"
    }

    fun startListening() {
        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (drivers.isEmpty()) {
            Log.d("MicrobitColorInput", "找不到 micro:bit 裝置")
            return
        }

        val driver = drivers[0]
        val connection = usbManager.openDevice(driver.device)
        if (connection == null) {
            val permissionIntent = PendingIntent.getBroadcast(
                context, 0, Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            val filter = IntentFilter(ACTION_USB_PERMISSION)
            ContextCompat.registerReceiver(
                context,
                usbReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            usbManager.requestPermission(driver.device, permissionIntent)
            return
        }

        usbPort = driver.ports[0]
        usbPort?.open(connection)
        usbPort?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        // 啟動 coroutine 讀資料
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(64)
            while (true) {
                val len = usbPort?.read(buffer, 1000) ?: 0
                if (len > 0) {
                    val data = String(buffer, 0, len, StandardCharsets.UTF_8).trim().uppercase()
                    val color = when (data) {
                        "RED" -> Color.Red
                        "YELLOW" -> Color.Yellow
                        "BLUE" -> Color.Blue
                        "GREEN" -> Color.Green
                        else -> null
                    }
                    if (color != null) {
                        colorState = color
                    }
                }
            }
        }
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USB_PERMISSION) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let { Log.d("USB", "已授權 ${it.deviceName}") }
                    } else {
                        Log.d("USB", "USB 權限被拒絕")
                    }
                }
            }
        }
    }

    fun stopListening() {
        usbPort?.close()
    }
}
