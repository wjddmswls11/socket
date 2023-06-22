package com.example.socketchat.data

data class NtDestroyPartyResponse(
    val cmd: String,
    val errInfo: ErrInfoNtDestroyParty,
    val data: DestroyPartyData
)

data class ErrInfoNtDestroyParty(
    val errNo: Int,
    val errMsg: String
)

data class DestroyPartyData(
    val msgNo: Long,
    val remainReadCount: Int,
    val partyNo: Int
)