package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.OneOnOneChatLogData
import com.example.socketchat.data.Re1On1ChatLog
import com.example.socketchat.retrofit.NetworkManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class OneOnOneApiViewModel : ViewModel() {
    private val service = NetworkManager.retrofit()


    //회원과의 1:1 채팅 로그 요청
    private val _oneOnOneChatLogFlow = MutableSharedFlow<ArrayList<Re1On1ChatLog>>()
    val oneOnOneChatLogFlow: SharedFlow<ArrayList<Re1On1ChatLog>>
        get() = _oneOnOneChatLogFlow

    //회원과의 1:1 채팅로그 요청
    fun fetchOneOnOneChat(fromMemNo: Int, toMemNo: Int, lastMsgNo: Long, sortType: Boolean) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchOneOnOneChat", "Error: ${throwable.localizedMessage}")
        }) {
            val request = OneOnOneChatLogData(
                fromMemNo = fromMemNo,
                toMemNo = toMemNo,
                lastMsgNo = lastMsgNo,
                countPerPage = 20,
                sortType = sortType
            )
            Log.d("1:1 채팅 로그", "OneOnOneChat Request: $request")
            val response = service.postOneOnOneChatLog(request)
            _oneOnOneChatLogFlow.emit(arrayListOf(response))
            Log.d("1:1 채팅 로그", "OneOnOneChat Response: $response")
        }

    }

}

