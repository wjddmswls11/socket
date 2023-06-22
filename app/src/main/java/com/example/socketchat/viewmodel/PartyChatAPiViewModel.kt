package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.PartyChatLogData
import com.example.socketchat.data.RePartyChatLog
import com.example.socketchat.retrofit.NetworkManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class PartyChatAPiViewModel : ViewModel() {

    private val service = NetworkManager.retrofit()

    //회원과의 파티 채팅 로그 요청
    private val _partyChatLogFlow = MutableSharedFlow<ArrayList<RePartyChatLog>>()
    val partyChatLogFlow: SharedFlow<ArrayList<RePartyChatLog>>
        get() = _partyChatLogFlow


    //회원과의 파티채팅로그 요청
    fun fetchPartyChatLog(partyNo: Int, rqMemNo: Int, lastMsgNo: Long, sortType: Boolean) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchPartyChatLog", "Error: ${throwable.localizedMessage}")
        }) {
            val request = PartyChatLogData(
                partyNo = partyNo,
                rqMemNo = rqMemNo,
                lastMsgNo = lastMsgNo,
                countPerPage = 30,
                sortType = sortType
            )
            Log.d("SummaryViewModel", "PartyChatLog Request: $request")
            val response = service.postPartyChatLog(request)
            _partyChatLogFlow.emit(arrayListOf(response))
            Log.d("SummaryViewModel", "PartyChatLog Response: $response")

        }
    }

}