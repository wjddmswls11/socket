package com.example.socketchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class NtUserLeavedPartyChatResponse(
    val cmd: String,
    val errInfo: ErrorInfoUserLeaved,
    val data: NtUserLeavedPartyUserLeavedData
) : Parcelable

@Parcelize
data class ErrorInfoUserLeaved(
    val errNo: Int,
    val errMsg: String
) : Parcelable

@Parcelize
data class NtUserLeavedPartyUserLeavedData(
    val msgNo: Long,
    val remainReadCount: Int,
    val partyNo: Int,
    val memNo: Int
) : Parcelable