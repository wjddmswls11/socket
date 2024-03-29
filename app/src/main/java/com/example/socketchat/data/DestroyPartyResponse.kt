package com.example.socketchat.data

data class DestroyPartyResponse(
    val cmd: String,
    val errInfo: ErrInfoDestroyParty,
    val data: NtDestroyPartyData
)

data class ErrInfoDestroyParty(
    val errNo: Int,
    val errMsg: String
)

data class NtDestroyPartyData(
    val msgNo: Long,
    val remainReadCount: Int,
    val partyNo: Int
)