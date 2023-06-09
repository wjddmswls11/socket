package com.example.socketchat.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReLeavePartyResponse(
    val cmd: String,
    val errInfo: ErrorInfoLeave,
    val data: LeavePartyData
) : Parcelable

@Parcelize
data class ErrorInfoLeave(
    val errNo: Int,
    val errMsg: String
): Parcelable

@Parcelize
data class LeavePartyData(
    val msgNo: Long,
    val partyNo: Int,
    val memNo: Int
): Parcelable

