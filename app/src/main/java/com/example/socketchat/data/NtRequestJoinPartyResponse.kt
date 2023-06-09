package com.example.socketchat.data

import java.io.Serializable

data class NtRequestJoinPartyResponse(
    val cmd: String,
    val errInfo: JoinErrInfo,
    val data: PartyInfo
) : Serializable


data class JoinErrInfo(
    val errNo: Int,
    val errMsg: String
)

data class PartyInfo(
    val SummaryPartyInfo: SummaryPartyInfoNt,
    val RqUserInfo: RqUserInfo
) : Serializable

data class SummaryPartyInfoNt(
    val partyNo: Int,
    val memNo: Int,
    val mainPhotoUrl: String,
    val title: String,
    val location: String,
    val curMemberCount: Int,
    val maxMemberCount: Int,
    val startTime: Long,
    val endTime: Long,
    val isAutoJoin: Boolean,
    val createAt: Long
) : Serializable

data class RqUserInfo(
    val memNo: Int,
    val nickName: String,
    val mainProfileUrl: String
) : Serializable
