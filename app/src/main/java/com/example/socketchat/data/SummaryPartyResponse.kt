package com.example.socketchat.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class SummaryPartyResponse(
    val cmd: String,
    @SerializedName("errInfo") val errorInfo: ErrorInfo,
    val data: List<Party>
) : Parcelable

@Parcelize
data class ErrorInfo(
    val errNo: Int,
    val errMsg: String
) : Parcelable

@Parcelize
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
) : Parcelable
