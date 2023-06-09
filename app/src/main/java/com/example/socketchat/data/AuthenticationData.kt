package com.example.socketchat.data

import com.google.gson.annotations.SerializedName

data class ErrorDetails(
    @SerializedName("errNo") val errNo: Int,
    @SerializedName("errMsg") val errMsg: String
)

data class SummaryUserInfo(
    @SerializedName("memNo") val memNo: Int,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("mainProfileUrl") val mainProfileUrl: String
)

data class Data(
    @SerializedName("summaryUserInfo") val summaryUserInfo: SummaryUserInfo,
    @SerializedName("result") val result: Int
)

data class ReAuthUser(
    @SerializedName("cmd") val cmd: String,
    @SerializedName("errInfo") val errorDetails: ErrorDetails,
    @SerializedName("data") val data: Data
)
