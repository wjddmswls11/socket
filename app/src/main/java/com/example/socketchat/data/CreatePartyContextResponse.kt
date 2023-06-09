package com.example.socketchat.data

data class CreatePartyContextResponse(
    val cmd: String,
    val errInfo: PartyErrorInfo,
    val data: PartyData
)

data class PartyErrorInfo(
    val errNo: Int,
    val errMsg: String
)

data class PartyData(
    val partyNo: Int
)
