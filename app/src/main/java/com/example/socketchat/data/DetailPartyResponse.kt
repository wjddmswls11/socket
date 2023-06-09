package com.example.socketchat.data

data class DetailPartyResponse(
    val cmd: String,
    val errInfo: ErrInfo,
    val data: DetailPartyData
)

data class ErrInfo(
    val errNo: Int,
    val errMsg: String
)

data class DetailPartyData(
    val summaryPartyInfo: SummaryPartyInfoDetail,
    val subPhotoUrlList: List<String>,
    val questContent: String,
    val state: Int,
    val partyServerID: Long
)

data class SummaryPartyInfoDetail(
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
)

