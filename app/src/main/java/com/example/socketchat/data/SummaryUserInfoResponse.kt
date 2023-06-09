package com.example.socketchat.data

import java.io.Serializable

data class SummaryUserInfoResponse(
    val cmd: String,
    val errInfo: SummaryUserErrInfo,
    val data: UserInfoData
) : Serializable

data class SummaryUserErrInfo(
    val errNo: Int,
    val errMsg: String
) : Serializable

data class UserInfoData(
    val memNo: Int,
    val nickName: String,
    val mainProfileUrl: String
) : Serializable
