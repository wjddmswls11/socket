package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.socket.SocketManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PartyChatViewModel : ViewModel() {
    private val socketManager = SocketManager

    //파티채팅
    fun sendPartyChat(msg: String, fromMemNo : Int, partyNo : Int){
        val requestData = JSONObject().apply {
            put("cmd", "RqPartyTextChat")
            put("data", JSONObject().apply {
                put("textChatInfo", JSONObject().apply {
                    put("msg", msg)
                })
                put("commonRqPartyChatInfo", JSONObject().apply {
                    put("replyMsgNo", 0)
                    put("fromMemNo", fromMemNo)
                    put("partyNo", partyNo)
                })
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit("Party", requestData)
    }


    //파티채팅
    private val _partyChatFlow = MutableSharedFlow<PartyChatResponse>()
    val partyChatFlow: SharedFlow<PartyChatResponse>
        get() = _partyChatFlow


    //파티이벤트
    fun setupParty() {
        socketManager.socket?.on("Party") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    partyChatResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }

    //파티채팅
    private fun partyChatResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: PartyChatResponse  $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {

                //파티채팅
                "RePartyTextChat", "NtPartyTextChat" -> {
                    val response = Gson().fromJson(data, PartyChatResponse::class.java)
                    _partyChatFlow.emit(response)
                    Log.d("ChatViewModel", "partyChatResponse RePartyTextChat: $response")
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }
}