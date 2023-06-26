package com.example.socketchat.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Exception
import java.net.URISyntaxException

object SocketManager {
    private const val SOCKET_URL = "http://61.80.148.23:3001/"
    private val socketConnectStatue = MutableStateFlow("연결중")
    val connectStatue = socketConnectStatue.asStateFlow()
    var socket: Socket? = null
        private set
    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }

    fun connect() {
        initializeSocketIfNeeded()

        socket?.connect()



        socket?.on(Socket.EVENT_CONNECT_ERROR) {args ->
            socketConnectStatue.value = "연결오류"
            reconnectIfNeeded()
            val exception = args[0] as Exception
            Log.d("jej", "Connection Error: ${exception.message}")
        }
        socket?.on(Socket.EVENT_ERROR) {args ->
            socketConnectStatue.value = "에러"
            reconnectIfNeeded()
            val exception = args[0] as Exception
            Log.d("jej", "Error: ${exception.message}, Cause: ${exception.cause}")
        }
        socket?.on(Socket.EVENT_DISCONNECT) {
            socketConnectStatue.value = "연결해제"
            reconnectIfNeeded()
            Log.d("jej", socketConnectStatue.value)
        }
        socket?.on(Socket.EVENT_CONNECT) {
            socketConnectStatue.value = "연결됨"
            Log.d("jej",  socketConnectStatue.value)
        }
        socket?.on(Socket.EVENT_RECONNECT_ATTEMPT) {
            socketConnectStatue.value = "연결시도"
            Log.d("jej", "${connectStatue.value}")
        }
        socket?.on(Socket.EVENT_RECONNECTING) {
            socketConnectStatue.value = "재연결됨"
            Log.d("jej", socketConnectStatue.value)
        }
        socket?.on(Socket.EVENT_RECONNECT_ERROR) {
            socketConnectStatue.value = "재연결에러"
            Log.d("jej", socketConnectStatue.value)
        }





    }

    private fun initializeSocketIfNeeded() {
        if (socket == null) {
            try {
                socket = IO.socket(SOCKET_URL)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
    }

    private fun reconnectIfNeeded() {
        if (!isConnected()) {
            socket?.connect()
        }
    }

    fun disconnect() {
        socket?.off(Socket.EVENT_CONNECT_ERROR)
        socket?.off(Socket.EVENT_ERROR)
        socket?.off(Socket.EVENT_DISCONNECT)
        socket?.off(Socket.EVENT_CONNECT)
        socket?.off(Socket.EVENT_RECONNECT_ATTEMPT)
        socket?.off(Socket.EVENT_RECONNECTING)
        socket?.off(Socket.EVENT_RECONNECT_ERROR)

        socket?.disconnect()
        socket = null
    }
}