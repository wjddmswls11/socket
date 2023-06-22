package com.example.socketchat.retrofit


import com.example.socketchat.data.CreatePartyContextResponse
import com.example.socketchat.data.CreateRoomRequest
import com.example.socketchat.data.DestroyPartyRequest
import com.example.socketchat.data.DestroyPartyResponse
import com.example.socketchat.data.DetailPartyRequest
import com.example.socketchat.data.DetailPartyResponse
import com.example.socketchat.data.MemNoData
import com.example.socketchat.data.OneOnOneChatLogData
import com.example.socketchat.data.PartyChatLogData
import com.example.socketchat.data.PartyMemberListRequest
import com.example.socketchat.data.Re1On1ChatLog
import com.example.socketchat.data.RePartyChatLog
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.data.SummaryPartyData
import com.example.socketchat.data.SummaryPartyResponse
import com.example.socketchat.data.SummaryUserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SummaryUserInfoInterface {

    //회원 요약정보 요청
    @POST("/RqSummaryUserInfo")
    suspend fun postSummaryUserInfo(@Body requestBody: MemNoData): SummaryUserInfoResponse

    //방 생성
    @POST("/RqCreatePartyContext")
    suspend fun postCreatePartyContext(@Body requestBody: CreateRoomRequest) : CreatePartyContextResponse

    //방 정보 요청
    @POST("/RqSummaryPartyList")
    suspend fun postSummaryPartyList(@Body requestBody : SummaryPartyData) : SummaryPartyResponse

    //방 삭제
    @POST("/RqDestroyPartyContext")
    suspend fun postDestroyPartyContext(@Body requestBody : DestroyPartyRequest) : DestroyPartyResponse

    //방 상세정보
    @POST("/RqDetailPartyContext")
    suspend fun postDetailPartyContext(@Body requestBody : DetailPartyRequest) : DetailPartyResponse

    //방 멤버 요청
    @POST("/RqPartyMemberList")
    suspend fun postPartyMemberList(@Body requestBody : PartyMemberListRequest) : RePartyMemberListResponse

    //회원과의 1:1 채팅로그 요청
    @POST("/Rq1On1ChatLog")
    suspend fun postOneOnOneChatLog(@Body requestBody : OneOnOneChatLogData) : Re1On1ChatLog

    //회원과의 파티채팅로그 요청
    @POST("/RqPartyChatLog")
    suspend fun postPartyChatLog(@Body requestBody : PartyChatLogData) : RePartyChatLog

}