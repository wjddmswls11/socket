package com.example.socketchat.socket

import android.util.Log
import com.example.socketchat.data.KickoutUserResponse
import com.example.socketchat.data.NtDestroyPartyResponse
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.data.NtUserLeavedPartyResponse
import com.example.socketchat.data.ReJoinPartyResponse
import com.example.socketchat.data.ReJoinPartyResultResponse
import com.example.socketchat.data.ReKickoutUserResponse
import com.example.socketchat.data.ReLeavePartyResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

object SocketDataRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val socketManager = SocketManager


    //파티떠나기
    fun sendLeaveParty(partyNo: Int, memNo: Int) {
        val requestData = JSONObject().apply {
            put("cmd", "RqLeaveParty")
            put("data", JSONObject().apply {
                put("partyNo", partyNo)
                put("memNo", memNo)
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit("Party", requestData)
    }

    //파티삭제
    private val privateDestroyPartyFlow = MutableSharedFlow<NtDestroyPartyResponse>()
    val destroyPartyFlow: SharedFlow<NtDestroyPartyResponse>
        get() = privateDestroyPartyFlow

    //유저강퇴
    private val _kickOutFlow = MutableSharedFlow<KickoutUserResponse>()
    val kickOutFlow: SharedFlow<KickoutUserResponse>
        get() = _kickOutFlow

    //유저강퇴
    private val _reKickOutFlow = MutableSharedFlow<ReKickoutUserResponse>()
    val reKickOutFlow: SharedFlow<ReKickoutUserResponse>
        get() = _reKickOutFlow

    //유저강퇴
    private val _ntUserLeaveFlow = MutableSharedFlow<NtUserLeavedPartyResponse>()
    val ntUserLeaveFlow: SharedFlow<NtUserLeavedPartyResponse>
        get() = _ntUserLeaveFlow

    //파티입장
    private val _joinPartyFlow = MutableSharedFlow<ReJoinPartyResponse>()
    val joinPartyFlow: SharedFlow<ReJoinPartyResponse>
        get() = _joinPartyFlow

    //파티입장
    private val _ntJoinPartyFlow = MutableStateFlow<List<NtRequestJoinPartyResponse>>(emptyList())
    val ntJoinPartyFlow: StateFlow<List<NtRequestJoinPartyResponse>>
        get() = _ntJoinPartyFlow

    //파티입장
    private val _reJoinPartyResult = MutableSharedFlow<ReJoinPartyResultResponse>()
    val reJoinPartyResult: SharedFlow<ReJoinPartyResultResponse>
        get() = _reJoinPartyResult


    //파티떠나기
    private val _leavePartyFlow = MutableStateFlow<List<ReLeavePartyResponse>>(emptyList())
    val leavePartyFlow: StateFlow<List<ReLeavePartyResponse>>
        get() = _leavePartyFlow


    //파티이벤트
    fun setupParty() {
        socketManager.socket?.on("Party") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    partyResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }



    //로비이벤트
    fun setupLobby() {
        socketManager.socket?.on("Lobby") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    joinPartyResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }


    //파티 cmd
    private fun partyResponse(data: String) {
        coroutineScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("SocketDataRepository", "Original JSON Response: ntDestroyPartyResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {

                //파티 삭제
                "NtDestroyParty" -> {
                    val response = Gson().fromJson(data, NtDestroyPartyResponse::class.java)
                    privateDestroyPartyFlow.emit(response)
                    Log.d(
                        "SocketDataRepository",
                        "deleteOneOnOneResponse NtDelete1On1Chat: $response"
                    )
                }

                //유저 강퇴
                "NtKickoutUser" -> {
                    val response = Gson().fromJson(data, KickoutUserResponse::class.java)
                    _kickOutFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "kickOutResponse RqKickoutUser: $response"
                    )
                }

                "ReKickoutUser" -> {
                    val response = Gson().fromJson(data, ReKickoutUserResponse::class.java)
                    _reKickOutFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "kickOutResponse ReKickoutUser: $response"
                    )

                }

                "NtUserLeavedParty" -> {
                    val response = Gson().fromJson(data, NtUserLeavedPartyResponse::class.java)
                    _ntUserLeaveFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "kickOutResponse NtUserLeavedParty: $response"
                    )
                }

                //파티 떠나기
                "ReLeaveParty" -> {
                    val response = Gson().fromJson(data, ReLeavePartyResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _leavePartyFlow.value.toMutableList()
                        currentList.add(response)
                        _leavePartyFlow.emit(currentList)
                    }
                }

                else -> {
                    Log.d("SocketDataRepository", "Unsupported command: $cmd")
                }
            }

        }
    }


    //로비 cmd
    private fun joinPartyResponse(data: String) {
        coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            // 로그에 원본 JSON 응답 출력
            Log.d("Socket Response", "Original JSON Response: joinPartyResponse $data")

            val jsonData = JSONObject(data)
            when(val cmd = jsonData.optString("cmd")) {
                //파티입장
                "ReJoinParty" -> {
                    val response = Gson().fromJson(data, ReJoinPartyResponse::class.java)
                    Log.d("파티입장", "ReJoinParty Response: $response")
                    // _joinPartyFlow에 추가
                    _joinPartyFlow.emit(response)
                }

                "NtRequestJoinParty" -> {
                    val response = Gson().fromJson(data, NtRequestJoinPartyResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _ntJoinPartyFlow.value.toMutableList()
                        currentList.add(response)
                        _ntJoinPartyFlow.emit(currentList)
                        Log.d("파티입장", "NtRequestJoinParty Response: $response")
                    }
                }

                "ReJoinPartyResult" -> {
                    val response = Gson().fromJson(data, ReJoinPartyResultResponse::class.java)
                    Log.d("파티입장", "ReJoinPartyResult Response: $response")
                    coroutineScope.launch {
                        _reJoinPartyResult.emit(response)
                    }
                }
                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }
}
