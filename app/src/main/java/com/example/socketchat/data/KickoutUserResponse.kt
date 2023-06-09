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
    val msgNo: Long,
    val remainReadCount: Int,
    val partyNo: Int,
    val ownerMemNo: Int,
    val kickoutMemNo: Int,
    val kickoutResult: Int
)  : Parcelable