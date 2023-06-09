package com.example.socketchat.data

data class NtUserLeavedPartyResponse(
    val cmd: String,
    val errInfo: ErrorInfoUserLeave,
    val data: NtUserLeavedPartyData
)

data class ErrorInfoUserLeave(
    val errNo: Int,
    val errMsg: String
)

data class NtUserLeavedPartyData(
    val msgNo: Long,
    val remainReadCount: Int,
    val partyNo: Int,
    val memNo: Int
)

