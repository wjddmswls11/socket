package com.example.socketchat.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SummaryPartyResponse(
    val cmd: String,
    @SerializedName("errInfo") val errorInfo: ErrorInfo,
    val data: List<Party>
) : Serializable

data class ErrorInfo(
    val errNo: Int,
    val errMsg: String
) : Serializable


data class Party(
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
