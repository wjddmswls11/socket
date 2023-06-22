package com.example.socketchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class NtUserJoinedPartyResponse(
    val cmd: String,
    val errInfo: ErrInfoJoin,
    val data: UserJoinedPartyDataJoin
)

data class ErrInfoJoin(
    val errNo: Int,
    val errMsg: String
)

data class UserJoinedPartyDataJoin(
    val msgNo: Long,
    val partyNo: Int,
    val joinUserInfo: JoinUserInfoJoin
)

@Parcelize
data class JoinUserInfoJoin(
    val memNo: Int,
    val nickName: String,
    val mainProfileUrl: String
) : Parcelable

