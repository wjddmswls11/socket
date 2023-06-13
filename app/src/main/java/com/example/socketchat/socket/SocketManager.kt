package com.example.socketchat.socket

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketManager {
    val socket: Socket by lazy {
        try {
            IO.socket("http://61.80.148.23:3001/").apply {
                connect()
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}