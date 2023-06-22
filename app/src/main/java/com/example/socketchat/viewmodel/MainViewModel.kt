package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.ReAuthUser
import com.example.socketchat.socket.SocketManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val socketManager = SocketManager

    private var isAuthResponseProcessed = false


    //회원인증
    fun sendAuthRequest(memNo: Int) {
        val requestData = JSONObject().apply {
            put("cmd", "RqAuthUser")
            put("data", JSONObject().apply {
                put("memNo", memNo)
            })
        }

        Log.d("Socket Request", "Sending RqAuthUser: $requestData")

        socketManager.socket?.emit("Lobby", requestData)
    }


    //회원인증
    private val _reAuthUserFlow = MutableSharedFlow<ReAuthUser>()
    val reAuthUserFlow: SharedFlow<ReAuthUser>
        get() = _reAuthUserFlow



    //로비이벤트
    fun setupLobby() {
        socketManager.socket?.on("Lobby") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    authUserSocketResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }

    //회원인증
    private fun authUserSocketResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                "ReAuthUser" -> {
                    val response = Gson().fromJson(data, ReAuthUser::class.java)
                    _reAuthUserFlow.emit(response)
                    isAuthResponseProcessed = true
                    Log.d("ChatViewModel", "authUserSocketResponse $response")
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }


}