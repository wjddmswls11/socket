package com.example.socketchat.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorDetails(
    @SerializedName("errNo") val errNo: Int,
    @SerializedName("errMsg") val errMsg: String
) : Parcelable

@Parcelize
data class SummaryUserInfo(
    @SerializedName("memNo") val memNo: Int,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("mainProfileUrl") val mainProfileUrl: String
): Parcelable

@Parcelize
data class Data(
    @SerializedName("summaryUserInfo") val summaryUserInfo: SummaryUserInfo,
    @SerializedName("result") val result: Int
) : Parcelable

@Parcelize
data class ReAuthUser(
    @SerializedName("cmd") val cmd: String,
    @SerializedName("errInfo") val errorDetails: ErrorDetails,
    @SerializedName("data") val data: Data
) : Parcelable
