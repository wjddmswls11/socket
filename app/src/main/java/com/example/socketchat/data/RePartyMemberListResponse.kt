package com.example.socketchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


@Parcelize
data class RePartyMemberListResponse(
    val cmd: String,
    val errInfo: ErrInfoMemberList,
    val data: List<MemberInfo>
) : Parcelable

data class ErrInfoMemberList(
    val errNo: Int,
    val errMsg: String
) : Serializable

@Parcelize
data class MemberInfo(
    val memNo: Int,
    val nickName: String,
    val mainProfileUrl: String
) : Parcelable