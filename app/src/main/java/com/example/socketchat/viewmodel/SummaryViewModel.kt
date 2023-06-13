package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.CreatePartyContextResponse
import com.example.socketchat.data.CreateRoomRequest
import com.example.socketchat.data.DestroyPartyRequest
import com.example.socketchat.data.DetailPartyRequest
import com.example.socketchat.data.DetailPartyResponse
import com.example.socketchat.data.MemNoData
import com.example.socketchat.data.OneOnOneChatLogData
import com.example.socketchat.data.Party
import com.example.socketchat.data.PartyChatLogData
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.data.PartyMemberListRequest
import com.example.socketchat.data.Re1On1ChatLog
import com.example.socketchat.data.RePartyChatLog
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.data.SummaryPartyData
import com.example.socketchat.data.SummaryPartyInfo
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.retrofit.NetworkManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SummaryViewModel : ViewModel() {

    private val service = NetworkManager.retrofit()

    //사용자 정보 요청
    private val _summarySharedFlow = MutableSharedFlow<ArrayList<SummaryUserInfoResponse>>()
    val summarySharedFlow: SharedFlow<ArrayList<SummaryUserInfoResponse>>
        get() = _summarySharedFlow

    //방 생성
    private val _createPartyFlow = MutableSharedFlow<CreatePartyContextResponse>()
    val createPartyFlow: SharedFlow<CreatePartyContextResponse>
        get() = _createPartyFlow

    //방 정보요청
    private val _summaryListSharedFlow = MutableSharedFlow<List<Party>>()
    val summaryListSharedFlow: SharedFlow<List<Party>>
        get() = _summaryListSharedFlow

    //방 상세정보
    private val _detailPartyContext = MutableSharedFlow<ArrayList<DetailPartyResponse>>()
    val detailPartyContext: SharedFlow<ArrayList<DetailPartyResponse>>
        get() = _detailPartyContext

    //방 멤버 요청
    private val _partyMemberList = MutableSharedFlow<ArrayList<RePartyMemberListResponse>>()
    val partyMemberList: SharedFlow<ArrayList<RePartyMemberListResponse>>
        get() = _partyMemberList

    //회원과의 1:1 채팅 로그 요청
    private val _oneOnOneChatLogFlow = MutableSharedFlow<ArrayList<Re1On1ChatLog>>()
    val oneOnOneChatLogFlow: SharedFlow<ArrayList<Re1On1ChatLog>>
        get() = _oneOnOneChatLogFlow

    //회원과의 파티 채팅 로그 요청
    private val _partyChatLogFlow = MutableSharedFlow<ArrayList<RePartyChatLog>>()
    val partyChatLogFlow: SharedFlow<ArrayList<RePartyChatLog>>
        get() = _partyChatLogFlow


    private val createPartyLoading = MutableLiveData<Boolean>()
    private val partyListLoading = MutableLiveData<Boolean>()
    private val destroyPartyLoading = MutableLiveData<Boolean>()
    private val detailPartyLoading = MutableLiveData<Boolean>()
    private val partyMemberLoading = MutableLiveData<Boolean>()
    private val oneOnOneChatLoading = MutableLiveData<Boolean>()
    private val partyChatLoading = MutableLiveData<Boolean>()




    //사용자 정보 요청
    fun fetchSummaryUserInfo(currentUserMemNo: Int?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchSummaryUserInfo", "Error: ${throwable.localizedMessage}")
        }) {

            val memNos = listOf(10009, 10010, 10011, 10012).toMutableList()

            currentUserMemNo?.let { currentUserMemNo ->
                memNos.remove(currentUserMemNo)
            }
            Log.d("SummaryViewModel first", "Fetching data for memNo: $currentUserMemNo")
            Log.d("SummaryViewModel first", "Fetching data for memNo: $memNos")

            val responses = ArrayList<SummaryUserInfoResponse>()

            memNos.forEach { num ->
                Log.d("SummaryViewModel", "Fetching data for memNo: $num")
                val response = service.postSummaryUserInfo(MemNoData(memNo = num))
                responses.add(response)
            }
            _summarySharedFlow.emit(responses)
        }

    }


    //파티 생성 요청
    fun fetchCreatePartyContext(
        memNo: Int,
        mainPhotoUrl: String,
        title: String,
        maxMemberCount: Int,
        isAutoJoin: Boolean,
        questContent: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchCreatePartyContext", "Error: ${throwable.localizedMessage}")
            createPartyLoading.value = false
        }) {
            createPartyLoading.value = true

            val defaultPhotoUrl =
                "https://demo.ycart.kr/shopboth_farm_max5_001/bbs/view_image.php?fn=http%3A%2F%2Fdemo.ycart.kr%2Fshopboth_cosmetics_001%2Fdata%2Feditor%2F1612%2Fcd2f39a0598c81712450b871c218164f_1482469221_493.jpg"

            val summaryPartyInfo = SummaryPartyInfo(
                memNo = memNo,
                mainPhotoUrl = if (mainPhotoUrl.isBlank()) defaultPhotoUrl else mainPhotoUrl,
                title = title,
                location = "서구 마륵동",
                maxMemberCount = maxMemberCount,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis(),
                isAutoJoin = isAutoJoin
            )

            val subPhotoUrlList = listOf(
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fkmong.com%2Fportfolio%2Fview%2F10972&psig=AOvVaw1QRGeIV66CEaQIx_Qgd3ih&ust=1682829365123000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCOD5zYaizv4CFQAAAAAdAAAAABAE",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.leagueoflegends.com%2Fko-kr%2Fchampions%2Fzed%2F&psig=AOvVaw3dPMEtuIrslBiQ1Tmkm92u&ust=1682829392161000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCKDlmJOizv4CFQAAAAAdAAAAABAE",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fkr.freepik.com%2Fphotos%2F%25EC%25BA%2594%25EB%2594%2594&psig=AOvVaw28t0cJAk2wY9TrX7u1iviS&ust=1682829409643000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCKj12Jyizv4CFQAAAAAdAAAAABAN"
            )

            val questContent = questContent

            val createRoomRequest = CreateRoomRequest(
                summaryPartyInfo = summaryPartyInfo,
                subPhotoUrlList = subPhotoUrlList,
                questContent = questContent
            )

            val response = service.postCreatePartyContext(
                createRoomRequest
            )

            if (response.errInfo.errNo == 0) {
                _createPartyFlow.emit(response)
                Log.d("파티 생성 fetchCreatePartyContext", "$response")
            }else{
                Log.d(
                    "파티 생성 fetchCreatePartyContext",
                    "Failed to fetch party list: ${response.errInfo.errMsg}"
                )
            }
        }
    }


    //방 정보 요청
    fun fetchSummaryPartyList() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchSummaryPartyList", "Error: ${throwable.localizedMessage}")
            partyListLoading.value = false
        }) {
            partyListLoading.value = true
            val response = service.postSummaryPartyList(
                SummaryPartyData(
                    dongCode = "1",
                    timeStamp = System.currentTimeMillis(),
                    countPerPage = 30
                )
            )
            if (response.errorInfo.errNo == 0) {
                _summaryListSharedFlow.emit(response.data)
                Log.i("aaa", "data size : ${response.data.size}")
                Log.d("파티 목록 SummaryViewModel _summaryListSharedFlow", "$response")
            } else {
                Log.d(
                    "파티 목록 SummaryViewModel",
                    "Failed to fetch party list: ${response.errorInfo.errMsg}"
                )
            }
        }
    }

    //파티 삭제 요청
    fun fetchDestroyParty(partyNo: Int, ownerMemNo: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchDestroyParty", "Error: ${throwable.localizedMessage}")
            destroyPartyLoading.value = false
        }) {
            destroyPartyLoading.value = true
            val response = service.postDestroyPartyContext(
                DestroyPartyRequest(partyNo, ownerMemNo))
            if (response.errInfo.errNo == 0) {
                Log.d(
                    "SummaryViewModel",
                    "Party destruction successful for partyNo: $response"
                )
                fetchSummaryPartyList()
            } else {
                Log.d(
                    "SummaryViewModel", "Party destruction failed for partyNo: ${response.errInfo.errMsg}"
                )
            }
        }
    }

    //방 상세정보 요청
    fun fetchDetailParty(partyNo: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchDetailParty", "Error: ${throwable.localizedMessage}")
            detailPartyLoading.value = false
        }) {
            detailPartyLoading.value = true
            val request = DetailPartyRequest(partyNo)
            val response = service.postDetailPartyContext(request)
            if (response.errInfo.errNo == 0) {
                val detailPartyResponses = arrayListOf(response)
                _detailPartyContext.emit(detailPartyResponses)
                Log.d("SummaryViewModel", "Detail party context fetched successfully")
                Log.d("SummaryViewModel", "Response: $response")
            } else {
                Log.d(
                    "SummaryViewModel",
                    "Failed to fetch detail party context: ${response.errInfo.errMsg}"
                )
            }
        }
    }

    //방 멤버 요청
    fun fetchPartyMember(partyNo: Int, ownerMemNo: Int) {
        Log.i("aaaaa", "call 1")
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchPartyMember", "Error: ${throwable.localizedMessage}")
            partyMemberLoading.value = false
        }) {
            partyMemberLoading.value = true
            val request = PartyMemberListRequest(
                partyNo = partyNo,
                ownerMemNo = ownerMemNo,
                timeStamp = System.currentTimeMillis(),
                CountPerPage = 30
            )
            val response = service.postPartyMemberList(request)
            Log.i("aaaaa", "call 3")
            _partyMemberList.emit(arrayListOf(response))

            Log.d("SummaryViewModel", "Party Member List Response: $response")
        }
        Log.i("aaaaa", "call 2")
    }


    //회원과의 1:1 채팅로그 요청
    fun fetchOneOnOneChat(fromMemNo: Int, toMemNo: Int, lastMsgNo : Long, sortType : Boolean) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchOneOnOneChat", "Error: ${throwable.localizedMessage}")
            oneOnOneChatLoading.value = false
        }) {
            oneOnOneChatLoading.value = true
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

    //회원과의 파티채팅로그 요청
    fun fetchPartyChatLog(partyNo: Int, rqMemNo: Int, lastMsgNo: Long, sortType: Boolean) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchPartyChatLog", "Error: ${throwable.localizedMessage}")
            partyChatLoading.value = false
        }) {
            partyChatLoading.value = true
            val request = PartyChatLogData(
                partyNo = partyNo,
                rqMemNo = rqMemNo,
                lastMsgNo = lastMsgNo,
                countPerPage = 30,
                sortType = sortType
            )
            Log.d("SummaryViewModel", "PartyChatLog Request: $request")
            val response = service.postPartyChatLog(request)

            val filteredData = mutableListOf<PartyChatResponse>()
            for (item in response.data) {
                if (item.cmd == "RePartyTextChat") {
                    filteredData.add(item)
                }
            }
            if (filteredData.isNotEmpty()) {
                _partyChatLogFlow.emit(arrayListOf(response.copy(data = filteredData)))
                Log.d("SummaryViewModel", "PartyChatLog Response: $response")
            }
        }
    }
}

