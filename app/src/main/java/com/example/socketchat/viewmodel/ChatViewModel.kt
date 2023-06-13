package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.DeleteOneOnOneResponseData
import com.example.socketchat.data.KickoutUserResponse
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.data.ReAuthUser
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.data.NtUserJoinedPartyResponse
import com.example.socketchat.data.NtUserLeavedPartyChatResponse
import com.example.socketchat.data.NtUserLeavedPartyResponse
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.data.ReJoinPartyResponse
import com.example.socketchat.data.ReJoinPartyResultResponse
import com.example.socketchat.data.ReKickoutUserResponse
import com.example.socketchat.data.ReLeavePartyResponse
import com.example.socketchat.socket.SocketManager
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

class ChatViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val socketManager = SocketManager

    //회원인증
    private val _reAuthUserFlow = MutableSharedFlow<ReAuthUser>()
    val reAuthUserFlow: SharedFlow<ReAuthUser>
        get() = _reAuthUserFlow

    //1:1채팅
    private val _oneOnOneFlow = MutableSharedFlow<Nt1On1TextChat>()
    val oneOnOneFlow: SharedFlow<Nt1On1TextChat>
        get() = _oneOnOneFlow

    //파티입장
    private val _joinPartyFlow = MutableSharedFlow<ReJoinPartyResponse>()
    val joinPartyFlow: SharedFlow<ReJoinPartyResponse>
        get() = _joinPartyFlow

    //파티입장
    private val _ntJoinPartyFlow = MutableSharedFlow<NtRequestJoinPartyResponse>()
    val ntJoinPartyFlow: SharedFlow<NtRequestJoinPartyResponse>
        get() = _ntJoinPartyFlow

    //파티입장
    private val _reJoinPartyResult = MutableSharedFlow<ReJoinPartyResultResponse>()
    val reJoinPartyResult : SharedFlow<ReJoinPartyResultResponse>
        get() = _reJoinPartyResult

    //파티장 파티참여 수락
    private val _ntUserJoinedPartyFlow =
        MutableSharedFlow<NtUserJoinedPartyResponse>()
    val ntUserJoinedPartyFlow: SharedFlow<NtUserJoinedPartyResponse>
        get() = _ntUserJoinedPartyFlow


    //파티채팅
    private val _partyChatFlow = MutableSharedFlow<PartyChatResponse>()
    val partyChatFlow: SharedFlow<PartyChatResponse>
        get() = _partyChatFlow


    //파티떠나기
    private val _leavePartyFlow = MutableStateFlow<List<ReLeavePartyResponse>>(emptyList())
    val leavePartyFlow: StateFlow<List<ReLeavePartyResponse>>
        get() = _leavePartyFlow

    //파티떠나기
    private val _ntLeavePartyFlow =
        MutableStateFlow<List<NtUserLeavedPartyChatResponse>>(emptyList())
    val ntLeavePartyFlow: StateFlow<List<NtUserLeavedPartyChatResponse>>
        get() = _ntLeavePartyFlow

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

    //1:1채팅 삭제
    private val _deleteOneOnOneFlow = MutableSharedFlow<DeleteOneOnOneResponseData>()
    val deleteOneOnOneFlow : SharedFlow<DeleteOneOnOneResponseData>
        get() = _deleteOneOnOneFlow


    init {
        setupAuthUserSocketListeners()
        setupOneOnOneSocketListeners()
        setupJoinPartyResponse()
        setupAcceptParty()
        setupPartyChat()
        setupLeaveParty()
        setupKickOutUser()
        setupDeleteOneOnOneChat()
    }

    //회원인증
    private fun setupAuthUserSocketListeners() {
        socketManager.socket.on("Lobby") { args ->
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

    //1:1채팅
    private fun setupOneOnOneSocketListeners() {
        socketManager.socket.on("Lobby") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    processMessageReceived(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }

    //파티입장
    private fun setupJoinPartyResponse() {
        socketManager.socket.on("Lobby") { args ->
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


    //파티장파티참여 수락
    private fun setupAcceptParty() {
        socketManager.socket.on("Party") { args ->
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

    //파티채팅
    private fun setupPartyChat() {
        socketManager.socket.on("Party") { args ->
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

    //파티떠나기
    private fun setupLeaveParty() {
        socketManager.socket.on("Party") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    leavePartyResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }
    }


    //유저강퇴
    private fun setupKickOutUser() {
        socketManager.socket.on("Party") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as String
                    kickOutResponse(data)
                } catch (e: Exception) {
                    Log.d("Socket Response", "Invalid data format: Unable to parse JSON data")
                }
            } else {
                Log.d("Socket Response", "Invalid data format: args is empty")
            }
        }

    }


    //1:1채팅 삭제
    private fun setupDeleteOneOnOneChat(){
        socketManager.socket.on("Lobby") {args ->
            if (args.isNotEmpty()){
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


    //회원인증
    private fun authUserSocketResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: authUserSocketResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                "ReAuthUser" -> {
                    val response = Gson().fromJson(data, ReAuthUser::class.java)
                    _reAuthUserFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "authUserSocketResponse: ${_reAuthUserFlow.emit(response)}"
                    )

                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }


    //1:1채팅
    private fun processMessageReceived(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: processMessageReceived $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
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

    //파티입장
    private fun joinPartyResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {

            // 로그에 원본 JSON 응답 출력
            Log.d("Socket Response", "Original JSON Response: joinPartyResponse $data")

            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "ReJoinParty" -> {
                    val response = Gson().fromJson(data, ReJoinPartyResponse::class.java)
                    Log.d("파티입장", "ReJoinParty Response: $response")
                    // _joinPartyFlow에 추가
                    coroutineScope.launch {
                        _joinPartyFlow.emit(response)
                    }
                }

                "NtRequestJoinParty" -> {
                    val response = Gson().fromJson(data, NtRequestJoinPartyResponse::class.java)
                    Log.d(
                        "파티입장",
                        "NtRequestJoinParty Response: $response"
                    )
                    coroutineScope.launch {
                        _ntJoinPartyFlow.emit(response)
                    }
                }

                "ReJoinPartyResult" -> {
                    val response = Gson().fromJson(data, ReJoinPartyResultResponse::class.java)
                    Log.d(
                        "파티입장",
                        "ReJoinPartyResult Response: $response"
                    )
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

    //파티장 파티참여 수락
    private fun acceptPartyResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("수락 ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("수락 Socket Response", "Original JSON Response: acceptPartyResponse $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "NtUserJoinedParty" -> {
                    val response = Gson().fromJson(data, NtUserJoinedPartyResponse::class.java)
                    _ntUserJoinedPartyFlow.emit(response)
                    Log.d(
                        "수락  ChatViewModel",
                        "수락 NtRequestJoinPartyResponses: $response"
                    )
                }

                else -> {
                    Log.d("수락 Socket Response", "Unsupported command: $cmd")
                }
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


    //파티떠나기
    private fun leavePartyResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: kickOutResponse $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "ReLeaveParty" -> {
                    val response = Gson().fromJson(data, ReLeavePartyResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _leavePartyFlow.value.toMutableList()
                        currentList.add(response)
                        _leavePartyFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "partyChatResponse ReLeaveParty: ${_leavePartyFlow.value}"
                        )
                    }
                }

                "NtUserLeavedParty" -> {
                    val response = Gson().fromJson(data, NtUserLeavedPartyChatResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _ntLeavePartyFlow.value.toMutableList()
                        currentList.add(response)
                        _ntLeavePartyFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "partyChatResponse NtUserLeavedParty: ${_ntLeavePartyFlow.value}"
                        )
                    }
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }

    //유저강퇴
    private fun kickOutResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {

            Log.d("Socket Response", "Original JSON Response: kickOutResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                "NtKickoutUser" -> {
                    val response = Gson().fromJson(data, KickoutUserResponse::class.java)
                    _kickOutFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "kickOutResponse RqKickoutUser: ${_kickOutFlow.emit(response)}"
                    )
                }

                "ReKickoutUser" -> {
                    val response = Gson().fromJson(data, ReKickoutUserResponse::class.java)
                    _reKickOutFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "kickOutResponse ReKickoutUser: ${_reKickOutFlow.emit(response)}"
                    )

                }

                "NtUserLeavedParty" -> {
                    val response = Gson().fromJson(data, NtUserLeavedPartyResponse::class.java)
                    _ntUserLeaveFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "kickOutResponse NtUserLeavedParty: ${_ntUserLeaveFlow.emit(response)}"
                    )
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }

            }
        }
    }




    //1:1 채팅 삭제
    private fun deleteOneOnOneResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("ChatViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: deleteOneOnOneResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                "ReDelete1On1Chat" -> {
                    val response = Gson().fromJson(data, DeleteOneOnOneResponseData::class.java)
                    _deleteOneOnOneFlow.emit(response)
                    Log.d(
                        "ChatViewModel",
                        "deleteOneOnOneResponse DeleteOneOnOne: ${_deleteOneOnOneFlow.emit(response)}"
                    )
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }

            }
        }
    }
}