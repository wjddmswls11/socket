package com.example.socketchat.data

data class ReJoinPartyResultResponse(
    val cmd: String,
    val errInfo: ErrorInfoReJoinPartyResult,
    val data: DataInfoReJoinPartyResult
)

data class ErrorInfoReJoinPartyResult(
    val errNo: Int,
    val errMsg: String
)

data class DataInfoReJoinPartyResult(
    val isAccept: Boolean,
    val denyReason: Int,
    val rqJoinParty: PartyInfoReJoinPartyResult,
    val commonRe1On1ChatInfo: ChatInfoReJoinPartyResult
)

data class PartyInfoReJoinPartyResult(
    val partyNo: Int,
    val ownerMemNo: Int,
    val rqMemNo: Int
)

data class ChatInfoReJoinPartyResult(
    val msgNo: Long,
    val replyMsgNo: Int,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val toMemNo: Int
)

