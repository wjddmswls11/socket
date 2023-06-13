package com.example.socketchat.data

data class  ReJoinPartyResponse(
    val cmd: String,
    val errInfo: ReJoinJoinErrInfo,
    val data: ReJoinPartyResponseData
)

data class ReJoinJoinErrInfo(
    val errNo: Int,
    val errMsg: String
)


data class ReJoinPartyResponseData(
    val rqJoinParty: PartyInfoReJoin,
    val commonRe1On1ChatInfo: ChatInfo
)


data class PartyInfoReJoin(
    val partyNo: Int,
    val ownerMemNo: Int,
    val rqMemNo: Int
)

data class ChatInfo(
    val msgNo: Long,
    val replyMsgNo: Int,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val toMemNo: Int
)



