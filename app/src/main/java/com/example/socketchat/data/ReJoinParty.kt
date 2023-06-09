package com.example.socketchat.data

data class  ReJoinPartyResponse(
    val cmd: String,
    val errInfo: ReJoinJoinErrInfo,
    val data: ReJoinPartyResponseData
)

data class ReJoinPartyResponseData(
    val msgNo: Long,
    val partyNo: Int,
    val isAccept: Boolean,
    val denyReason: Int,
    val rqMemNo: Int
)

data class ReJoinJoinErrInfo(
    val errNo: Int,
    val errMsg: String
)
