package com.example.socketchat.data

data class ReKickoutUserResponse(
    val cmd: String,
    val errInfo: ErrorInfoReKick,
    val data: ReKickoutUserData
)

data class ErrorInfoReKick(
    val errNo: Int,
    val errMsg: String
)

data class ReKickoutUserData(
    val msgNo: Long,
    val remainReadCount: Int,
    val partyNo: Int,
    val ownerMemNo: Int,
    val kickoutMemNo: Int,
    val kickoutResult: Int
)

