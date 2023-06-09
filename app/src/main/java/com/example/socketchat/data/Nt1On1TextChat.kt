package com.example.socketchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


@Parcelize
data class Re1On1ChatLog(
    val errInfo: ErrInfoOneOnOneChat,
    val data: List<Nt1On1TextChat>
) : Parcelable


@Parcelize
data class ErrInfoOneOnOneChat(
    val errNo: Int,
    val errMsg: String
) : Parcelable


data class Nt1On1TextChat(
    val cmd: String,
    val errInfo: OneOnOneDataErrInfo,
    val data: OneOnOneDataData
) : Serializable

data class OneOnOneData(
    val msg: String
) : Serializable

data class CommonChatInfo(
    val msgNo: Long,
    val replyMsgNo: Int,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val toMemNo: Int
) : Serializable

data class OneOnOneDataErrInfo(
    val errNo: Int,
    val errMsg: String
) : Serializable

data class OneOnOneDataData(
    val textChatInfo: OneOnOneData,
    val commonRe1On1ChatInfo: CommonChatInfo
) : Serializable

