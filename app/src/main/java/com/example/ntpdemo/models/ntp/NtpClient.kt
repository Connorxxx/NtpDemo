package com.example.ntpdemo.models.ntp

import android.os.SystemClock
import com.example.ntpdemo.utils.logCat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NtpClient @Inject constructor() {

    private var socket = DatagramSocket()

    suspend fun syncTimeWithNtpServer(ntpServerAddress: String, port: Int = 123) = withContext(Dispatchers.IO) {
        runCatching {
            val ntpData = ByteArray(48)
            ntpData[0] = 0x1B.toByte()

            if (socket.isClosed) socket = DatagramSocket()

            socket.soTimeout = 2000

            val address = InetAddress.getByName(ntpServerAddress)
            val packet = DatagramPacket(ntpData, ntpData.size, address, port)
            val sendTime = System.currentTimeMillis()
            socket.send(packet)

            val receivePacket = DatagramPacket(ntpData, ntpData.size)
            socket.receive(receivePacket)
            val receiveTime = System.currentTimeMillis()
            val ntpTime = parseNtpResponse(ntpData)


            val timeDifference = ntpTime - (receiveTime + sendTime) / 2
            val time = System.currentTimeMillis() + timeDifference

          //  SystemClock.setCurrentTimeMillis(time).also { it.logCat() }
            time
        }
    }

    fun close() {
        socket.close()
    }

    private fun parseNtpResponse(data: ByteArray) = run {
        val offset = 32
        val seconds = data[offset].toLong() and 0xFF shl 24 or
                (data[offset + 1].toLong() and 0xFF shl 16) or
                (data[offset + 2].toLong() and 0xFF shl 8) or
                (data[offset + 3].toLong() and 0xFF)

        val fraction = data[offset + 4].toLong() and 0xFF shl 24 or
                (data[offset + 5].toLong() and 0xFF shl 16) or
                (data[offset + 6].toLong() and 0xFF shl 8) or
                (data[offset + 7].toLong() and 0xFF)

        // NTP 时间戳以 1900 年为基准，需要转换成 Unix 时间戳（以 1970 年为基准）

        (seconds - 2208988800L) * 1000 + fraction * 1000L / 0x100000000L
    }

}