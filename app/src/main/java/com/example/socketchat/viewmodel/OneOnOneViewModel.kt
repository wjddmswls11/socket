package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.DeleteOneOnOneResponseData
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.socket.SocketManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class OneOnOneViewModel : ViewModel() {
    private val socketManager = SocketManager


    //1:1채팅
    fun send1On1ChatRequest(msg: String, fromMemNo: Int, toMemNo: Int) {
        val requestData = JSONObject().apply {
            put("cmd", "Rq1On1TextChat")
            put("data", JSONObject().apply {
                put("textChatInfo", JSONObject().apply {
                    put("msg", msg)
                })
                put("commonRq1On1ChatInfo", JSONObject().apply {
                    put("replyMsgNo", 0)
                    put("fromMemNo", fromMemNo)
                    put("toMemNo", toMemNo)
                })
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit("Lobby", requestData)
    }


    //1:1채팅
    private val _oneOnOneFlow = MutableSharedFlow<Nt1On1TextChat>()
    val oneOnOneFlow: SharedFlow<Nt1On1TextChat>
        get() = _oneOnOneFlow

    //1:1채팅 삭제
    private val _deleteOneOnOneFlow = MutableSharedFlow<DeleteOneOnOneResponseData>()
    val deleteOneOnOneFlow : SharedFlow<DeleteOneOnOneResponseData>
        get() = _deleteOneOnOneFlow




    //로비이벤트
    fun setupLobby() {
        socketManager.socket?.on("Lobby") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    deleteOneOnOneResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }


    //로비 이벤트
    private fun deleteOneOnOneResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: deleteOneOnOneResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                //1:1 채팅 삭제
                "ReDelete1On1Chat", "NtDelete1On1Chat" -> {
                    val response = Gson().fromJson(data, DeleteOneOnOneResponseData::class.java)
                    _deleteOneOnOneFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "deleteOneOnOneResponse ReDelete1On1Chat: $response"
                    )
                }

                //1:1채팅
                "Re1On1TextChat", "Nt1On1TextChat" -> {
                    val response = Gson().fromJson(data, Nt1On1TextChat::class.java)
                    _oneOnOneFlow.emit(response)
                    Log.d("ChatViewModel", "1processMessageReceived: $response")
                }


                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }

            }
        }
    }

}