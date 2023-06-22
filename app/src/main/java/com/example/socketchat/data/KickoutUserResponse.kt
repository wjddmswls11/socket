package com.example.socketchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KickoutUserResponse(
    val cmd: String,
    val errInfo : ErrInfoKickOut,
    val data: RqKickOutUserData
) : Parcelable

@Parcelize
data class ErrInfoKickOut(
    val errNo: Int,
    val errMsg: String
) : Parcelable

@Parcelize
data class RqKickOutUserData(
    val ownerMemNo: Int,
    val kickoutMemNo: Int,
    val kickoutResult: Int,
    val commonRePartyChatInfo: CommonKickoutUser
)  : Parcelable

@Parcelize
data class CommonKickoutUser(
    val msgNo: Long,
    val replyMsgNo: Int,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val partyNo: Int
) : Parcelable