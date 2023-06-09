package com.example.socketchat.data

import com.google.gson.annotations.SerializedName

data class CreateRoomRequest(
    @SerializedName("summaryPartyInfo")
    val summaryPartyInfo: SummaryPartyInfo,
    @SerializedName("subPhotoUrlList")
    val subPhotoUrlList: List<String>,
    @SerializedName("questContent")
    val questContent: String
)

data class SummaryPartyInfo(
    @SerializedName("memNo")
    val memNo: Int,
    @SerializedName("mainPhotoUrl")
    val mainPhotoUrl: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("maxMemberCount")
    val maxMemberCount: Int,
    @SerializedName("startTime")
    val startTime: Long,
    @SerializedName("endTime")
    val endTime: Long,
    @SerializedName("isAutoJoin")
    val isAutoJoin: Boolean
)
