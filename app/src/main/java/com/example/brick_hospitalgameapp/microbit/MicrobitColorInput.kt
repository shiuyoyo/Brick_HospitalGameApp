package com.example.brick_hospitalgameapp.microbit

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets

class MicrobitColorInput(context: Context) {

    private val appContext = context.applicationContext
    private val usbManager: UsbManager =
        appContext.getSystemService(Context.USB_SERVICE) as UsbManager

    private var usbPort: UsbSerialPort? = null
    private var scope: CoroutineScope? = null
    private var readJob: Job? = null

    private var receiverRegistered = false

    // 你遊戲頁面用的顏色狀態
    var colorState by mutableStateOf<Color?>(null)
        private set

    // ✅ 新增：把偵錯資訊顯示到畫面上
    var statusText by mutableStateOf("尚未開始")
        private set

    companion object {
        private const val TAG = "MicrobitColorInput"
        private const val ACTION_USB_PERMISSION = "com.example.brick_hospitalgameapp.USB_PERMISSION"
    }

    fun startListening() {
        // 防止重複啟動
        if (readJob?.isActive == true) {
            statusText = "監聽中（readJob active）"
            return
        }

        // 先列出手機目前看到的所有 USB 裝置
        val devices = usbManager.deviceList.values.toList()
        statusText = buildString {
            append("USB 裝置數量: ${devices.size}\n")
            if (devices.isEmpty()) {
                append("未偵測到任何 USB 裝置\n")
                append("請確認：OTG/線材/轉接頭/供電/手機限制")
            } else {
                devices.forEach { d ->
                    append("• ${d.deviceName} (VID=${d.vendorId}, PID=${d.productId})\n")
                }
            }
        }

        if (devices.isEmpty()) {
            Log.d(TAG, "USB deviceList is empty")
            return
        }

        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (drivers.isEmpty()) {
            statusText += "\n\n找不到 USB Serial Driver\n" +
                    "（可能不是 CDC/Serial 模式，或目前裝置不被支援）"
            Log.d(TAG, "找不到 USB Serial 裝置 (micro:bit?)")
            return
        }

        // ⚠️ 先沿用你原本策略：取第一個 driver
        // 若你有多個 USB 裝置，未來我可以再幫你改成選 VID/PID 對應 micro:bit
        val driver = drivers[0]
        val device = driver.device

        // 沒權限就申請
        if (!usbManager.hasPermission(device)) {
            registerReceiverIfNeeded()
            statusText += "\n\n尚未授權 USB，正在請求權限…"
            val permissionIntent = PendingIntent.getBroadcast(
                appContext,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(device, permissionIntent)
            Log.d(TAG, "請求 USB 權限: ${device.deviceName}")
            return
        }

        val connection = usbManager.openDevice(device)
        if (connection == null) {
            statusText += "\n\nopenDevice 失敗（可能仍未授權或裝置忙碌）"
            Log.d(TAG, "openDevice 失敗（可能仍未授權或裝置忙碌）")
            return
        }

        try {
            usbPort = driver.ports.firstOrNull()
            if (usbPort == null) {
                statusText += "\n\ndriver.ports 為空（無可用 serial port）"
                Log.d(TAG, "driver.ports 為空")
                return
            }

            usbPort?.open(connection)
            usbPort?.setParameters(
                115200,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )

            // ✅ 低成本補強：某些裝置需要 DTR/RTS 才會開始吐資料
            try {
                usbPort?.dtr = true
                usbPort?.rts = true
            } catch (_: Exception) {}

            statusText += "\n\n已連線並開始讀取…(baud=115200)"

            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            readJob = scope?.launch {
                val buffer = ByteArray(64)
                while (isActive) {
                    val len = try {
                        usbPort?.read(buffer, 1000) ?: 0
                    } catch (e: Exception) {
                        Log.e(TAG, "read() 失敗: ${e.message}")
                        withContext(Dispatchers.Main) {
                            statusText += "\nread() 失敗: ${e.message}"
                        }
                        0
                    }

                    if (len > 0) {
                        val data = String(buffer, 0, len, StandardCharsets.UTF_8)
                            .trim()
                            .uppercase()

                        val color = when (data) {
                            "RED" -> Color.Red
                            "YELLOW" -> Color.Yellow
                            "BLUE" -> Color.Blue
                            "GREEN" -> Color.Green
                            else -> null
                        }

                        withContext(Dispatchers.Main) {
                            if (color != null) {
                                colorState = color
                                statusText = statusText.keepTopLines(14) + "\n最後收到: $data"
                                Log.d(TAG, "收到顏色: $data")
                            } else {
                                statusText = statusText.keepTopLines(14) + "\n最後收到(未知): $data"
                                Log.d(TAG, "收到未知資料: $data")
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "USB Serial 開始監聽 OK")
        } catch (e: Exception) {
            Log.e(TAG, "startListening 失敗: ${e.message}")
            statusText += "\n\nstartListening 失敗: ${e.message}"
            stopListening()
        }
    }

    fun stopListening() {
        try {
            readJob?.cancel()
            readJob = null
            scope?.cancel()
            scope = null
        } catch (_: Exception) {}

        try {
            usbPort?.close()
        } catch (_: Exception) {}
        usbPort = null

        unregisterReceiverIfNeeded()
        statusText = "已停止（未連線）"
        Log.d(TAG, "USB Serial 已停止")
    }

    fun rescan() {
        stopListening()
        startListening()
    }

    private fun registerReceiverIfNeeded() {
        if (receiverRegistered) return

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        ContextCompat.registerReceiver(
            appContext,
            usbReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        receiverRegistered = true
    }

    private fun unregisterReceiverIfNeeded() {
        if (!receiverRegistered) return
        try {
            appContext.unregisterReceiver(usbReceiver)
        } catch (_: Exception) {}
        receiverRegistered = false
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (intent.action != ACTION_USB_PERMISSION) return

            val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

            if (granted) {
                statusText = "USB 已授權: ${device?.deviceName}\n正在啟動監聽…"
                Log.d(TAG, "USB 已授權: ${device?.deviceName}")
                startListening()
            } else {
                statusText = "USB 權限被拒絕: ${device?.deviceName}"
                Log.d(TAG, "USB 權限被拒絕: ${device?.deviceName}")
            }
        }
    }
}

/** 防止 statusText 無限變長：只保留前 N 行 */
private fun String.keepTopLines(maxLines: Int): String {
    val lines = this.lines()
    return if (lines.size <= maxLines) this else lines.take(maxLines).joinToString("\n")
}
