package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.NtUserJoinedPartyResponse
import com.example.socketchat.socket.SocketManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class MenuViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val socketManager = SocketManager


    //파티장 파티참여 수락
    private val _ntUserJoinedPartyFlow =
        MutableStateFlow<List<NtUserJoinedPartyResponse>>(emptyList())
    val ntUserJoinedPartyFlow: StateFlow<List<NtUserJoinedPartyResponse>>
        get() = _ntUserJoinedPartyFlow



    //파티이벤트
    fun setupParty() {
        socketManager.socket?.on("Party") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    acceptPartyResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }

    //파티이벤트
    private fun acceptPartyResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("수락 ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("수락 Socket Response", "Original JSON Response: acceptPartyResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {

                //파티장 파티참여 수락, 방 생성
                "NtUserJoinedParty" -> {
                    val response = Gson().fromJson(data, NtUserJoinedPartyResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _ntUserJoinedPartyFlow.value.toMutableList()
                        currentList.add(response)
                        _ntUserJoinedPartyFlow.emit(currentList)
                        Log.d(
                            "수락  ChatViewModel",
                            "수락 NtRequestJoinPartyResponses: $response"
                        )
                    }
                }

                else -> {
                    Log.d("수락 Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }
}