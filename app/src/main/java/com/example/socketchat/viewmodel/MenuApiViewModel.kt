package com.example.socketchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socketchat.data.CreatePartyContextResponse
import com.example.socketchat.data.CreateRoomRequest
import com.example.socketchat.data.DestroyPartyRequest
import com.example.socketchat.data.DestroyPartyResponse
import com.example.socketchat.data.DetailPartyRequest
import com.example.socketchat.data.DetailPartyResponse
import com.example.socketchat.data.MemNoData
import com.example.socketchat.data.Party
import com.example.socketchat.data.PartyMemberListRequest
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.data.SummaryPartyData
import com.example.socketchat.data.SummaryPartyInfo
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.retrofit.NetworkManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MenuApiViewModel : ViewModel(){

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


    //파티 삭제
    private val _kickOutSharedFlow = MutableSharedFlow<ArrayList<DestroyPartyResponse>>()
    val kickOutSharedFlow: SharedFlow<ArrayList<DestroyPartyResponse>>
        get() = _kickOutSharedFlow


    //방 상세정보
    private val _detailPartyContext = MutableSharedFlow<ArrayList<DetailPartyResponse>>()
    val detailPartyContext: SharedFlow<ArrayList<DetailPartyResponse>>
        get() = _detailPartyContext


    //방 멤버 요청
    private val _partyMemberList = MutableSharedFlow<ArrayList<RePartyMemberListResponse>>()
    val partyMemberList: SharedFlow<ArrayList<RePartyMemberListResponse>>
        get() = _partyMemberList

    //사용자 정보 요청
    fun fetchSummaryUserInfo(currentUserMemNo: Int?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchSummaryUserInfo", "Error: ${throwable.localizedMessage}")
        }) {

            val memNos = listOf(10009, 10010, 10011, 10012).toMutableList()

            currentUserMemNo?.let { memNos.remove(it) }


            Log.d("SummaryViewModel first", "Fetching data for memNo: $currentUserMemNo")
            Log.d("SummaryViewModel first", "Fetching data for memNo: $memNos")

            val responses = ArrayList<SummaryUserInfoResponse>()

            for (num in memNos) {
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
        }) {

            val defaultPhotoUrl =
                "https://demo.ycart.kr/shopboth_farm_max5_001/bbs/view_image.php?fn=http%3A%2F%2Fdemo.ycart.kr%2Fshopboth_cosmetics_001%2Fdata%2Feditor%2F1612%2Fcd2f39a0598c81712450b871c218164f_1482469221_493.jpg"

            val summaryPartyInfo = SummaryPartyInfo(
                memNo = memNo,
                mainPhotoUrl = mainPhotoUrl.ifBlank { defaultPhotoUrl },
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
            } else {
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
        }) {
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
        }) {
            val response = service.postDestroyPartyContext(
                DestroyPartyRequest(partyNo, ownerMemNo)
            )
            if (response.errInfo.errNo == 0) {
                Log.d(
                    "SummaryViewModel",
                    "Party destruction successful for partyNo: $response"
                )
                _kickOutSharedFlow.emit(arrayListOf(response))


                fetchSummaryPartyList()
            } else {
                Log.d(
                    "SummaryViewModel",
                    "Party destruction failed for partyNo: ${response.errInfo.errMsg}"
                )
            }
        }
    }

    //방 상세정보 요청
    fun fetchDetailParty(partyNo: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchDetailParty", "Error: ${throwable.localizedMessage}")
        }) {
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
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e("fetchPartyMember", "Error: ${throwable.localizedMessage}")
        }) {
            val request = PartyMemberListRequest(
                partyNo = partyNo,
                ownerMemNo = ownerMemNo,
                timeStamp = System.currentTimeMillis(),
                CountPerPage = 30
            )
            val response = service.postPartyMemberList(request)
            if (response.errInfo.errNo == 0) {
                _partyMemberList.emit(arrayListOf(response))
            }
            Log.d("SummaryViewModel", "Party Member List Response: $response")
        }
    }
}