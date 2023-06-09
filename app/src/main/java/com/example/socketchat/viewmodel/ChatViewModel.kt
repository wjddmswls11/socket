package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.KickoutUserResponse
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.data.ReAuthUser
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.data.NtUserJoinedPartyResponse
import com.example.socketchat.data.NtUserLeavedPartyChatResponse
import com.example.socketchat.data.NtUserLeavedPartyResponse
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.data.ReJoinPartyResponse
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
import org.json.JSONException
import org.json.JSONObject

class ChatViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val socketManager = SocketManager

    //회원인증
    private val _reAuthUserFlow = MutableStateFlow<List<ReAuthUser>>(emptyList())
    val reAuthUserFlow: StateFlow<List<ReAuthUser>>
        get() = _reAuthUserFlow

    //1:1채팅
    private val _oneOnOneFlow = MutableStateFlow<List<Nt1On1TextChat>>(emptyList())
    val oneOnOneFlow: StateFlow<List<Nt1On1TextChat>>
        get() = _oneOnOneFlow

    //파티입장
    private val _joinPartyFlow = MutableSharedFlow<ReJoinPartyResponse>()
    val joinPartyFlow: SharedFlow<ReJoinPartyResponse>
        get() = _joinPartyFlow

    //파티입장
    private val _ntJoinPartyFlow = MutableSharedFlow<NtRequestJoinPartyResponse>()
    val ntJoinPartyFlow: SharedFlow<NtRequestJoinPartyResponse>
        get() = _ntJoinPartyFlow

    //파티장 파티참여 수락
    private val _ntUserJoinedPartyFlow =
        MutableStateFlow<List<NtUserJoinedPartyResponse>>(emptyList())
    val ntUserJoinedPartyFlow: StateFlow<List<NtUserJoinedPartyResponse>>
        get() = _ntUserJoinedPartyFlow


    //파티채팅
    private val _partyChatFlow = MutableStateFlow<List<PartyChatResponse>>(emptyList())
    val partyChatFlow: StateFlow<List<PartyChatResponse>>
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
    private val _kickOutFlow = MutableStateFlow<List<KickoutUserResponse>>(emptyList())
    val kickOutFlow: StateFlow<List<KickoutUserResponse>>
        get() = _kickOutFlow

    //유저강퇴
    private val _reKickOutFlow = MutableStateFlow<List<ReKickoutUserResponse>>(emptyList())
    val reKickOutFlow: StateFlow<List<ReKickoutUserResponse>>
        get() = _reKickOutFlow

    //유저강퇴
    private val _ntUserLeaveFlow = MutableStateFlow<List<NtUserLeavedPartyResponse>>(emptyList())
    val ntUserLeaveFlow: StateFlow<List<NtUserLeavedPartyResponse>>
        get() = _ntUserLeaveFlow


    init {
        setupAuthUserSocketListeners()
        setupOneOnOneSocketListeners()
        _oneOnOneFlow.value = emptyList()
        setupJoinPartyResponse()
        setupAcceptParty()
        setupPartyChat()
        setupLeaveParty()
        setupKickOutUser()
    }


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


    //회원인증
    private fun authUserSocketResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: authUserSocketResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                "ReAuthUser" -> {
                    val response = Gson().fromJson(data, ReAuthUser::class.java)
                    coroutineScope.launch {
                        val currentList = _reAuthUserFlow.value.toMutableList()
                        currentList.add(response)
                        _reAuthUserFlow.emit(currentList)
                        Log.d("ChatViewModel", "authUserSocketResponse: ${_reAuthUserFlow.value}")
                    }
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
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: processMessageReceived $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "Re1On1TextChat" -> {
                    val response = Gson().fromJson(data, Nt1On1TextChat::class.java)
                    coroutineScope.launch {
                        val currentList = _oneOnOneFlow.value.toMutableList()
                        currentList.add(response)
                        _oneOnOneFlow.emit(currentList)
                        Log.d("ChatViewModel", "1processMessageReceived: ${_oneOnOneFlow.value}")
                    }
                }

                "Nt1On1TextChat" -> {
                    val response = Gson().fromJson(data, Nt1On1TextChat::class.java)
                    coroutineScope.launch {
                        val currentList = _oneOnOneFlow.value.toMutableList()
                        currentList.add(response)
                        _oneOnOneFlow.emit(currentList)
                        Log.d("ChatViewModel", "2processMessageReceived: ${_oneOnOneFlow.value}")
                    }
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
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
        }) {

            // 로그에 원본 JSON 응답 출력
            Log.d("Socket Response", "Original JSON Response: joinPartyResponse $data")

            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "ReJoinParty" -> {
                    val response = Gson().fromJson(data, ReJoinPartyResponse::class.java)
                    Log.d("Socket Response joinPartyResponse", "ReAuthUser Response: $response")
                    // _joinPartyFlow에 추가
                    coroutineScope.launch {
                        _joinPartyFlow.emit(response)
                    }
                }

                "NtRequestJoinParty" -> {
                    val response = Gson().fromJson(data, NtRequestJoinPartyResponse::class.java)
                    Log.d("Socket Response joinPartyResponse", "NtRequestJoinParty Response: $response")
                    coroutineScope.launch {
                        _ntJoinPartyFlow.emit(response)
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
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: acceptPartyResponse $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "NtUserJoinedParty" -> {
                    val response = Gson().fromJson(data, NtUserJoinedPartyResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _ntUserJoinedPartyFlow.value.toMutableList()
                        currentList.add(response)
                        _ntUserJoinedPartyFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "NtRequestJoinPartyResponses: ${_ntUserJoinedPartyFlow.value}"
                        )
                    }
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }
            }
        }
    }


    //파티채팅
    private fun partyChatResponse(data: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
        }) {
            Log.d("Socket Response", "Original JSON Response: PartyChatResponse  $data")
            val jsonData = JSONObject(data)

            when (val cmd = jsonData.optString("cmd")) {
                "RePartyTextChat" -> {
                    val response = Gson().fromJson(data, PartyChatResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _partyChatFlow.value.toMutableList()
                        currentList.add(response)
                        _partyChatFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "partyChatResponse RePartyTextChat: ${_partyChatFlow.value}"
                        )
                    }

                }

                "NtPartyTextChat" -> {
                    val response = Gson().fromJson(data, PartyChatResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _partyChatFlow.value.toMutableList()
                        currentList.add(response)
                        _partyChatFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "partyChatResponse NtPartyTextChat: ${_partyChatFlow.value}"
                        )
                    }
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
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
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
            Log.e("SummaryViewModel", "Error: ${throwable.localizedMessage}")
        }) {

            Log.d("Socket Response", "Original JSON Response: kickOutResponse $data")
            val jsonData = JSONObject(data)
            when (val cmd = jsonData.optString("cmd")) {
                "NtKickoutUser" -> {
                    val response = Gson().fromJson(data, KickoutUserResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _kickOutFlow.value.toMutableList()
                        currentList.add(response)
                        _kickOutFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "kickOutResponse RqKickoutUser: ${_kickOutFlow.value}"
                        )
                    }
                }

                "ReKickoutUser" -> {
                    val response = Gson().fromJson(data, ReKickoutUserResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _reKickOutFlow.value.toMutableList()
                        currentList.add(response)
                        _reKickOutFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "kickOutResponse ReKickoutUser: ${_reKickOutFlow.value}"
                        )
                    }
                }

                "NtUserLeavedParty" -> {
                    val response = Gson().fromJson(data, NtUserLeavedPartyResponse::class.java)
                    coroutineScope.launch {
                        val currentList = _ntUserLeaveFlow.value.toMutableList()
                        currentList.add(response)
                        _ntUserLeaveFlow.emit(currentList)
                        Log.d(
                            "ChatViewModel",
                            "kickOutResponse NtUserLeavedParty: ${_ntUserLeaveFlow.value}"
                        )
                    }
                }

                else -> {
                    Log.d("Socket Response", "Unsupported command: $cmd")
                }

            }
        }
    }
}