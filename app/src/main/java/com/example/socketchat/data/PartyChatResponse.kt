package com.example.socketchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RePartyChatLog(
    val cmd : String,
    val errInfo: ErrInfoPartyChatLog,
    val data: List<PartyChatResponse>
) : Parcelable

@Parcelize
data class ErrInfoPartyChatLog(
    val errNo: Int,
    val errMsg: String
) : Parcelable

@Parcelize
data class PartyChatResponse(
    val cmd: String,
    val errInfo: ErrInfoPartyText,
    val data: DataPartyText
): Parcelable


@Parcelize
data class ErrInfoPartyText(
    val errNo: Int,
    val errMsg: String
) : Parcelable

@Parcelize
data class DataPartyText(
    val textChatInfo: TextChatInfo,
    val commonRePartyChatInfo: CommonRePartyChatInfo
) : Parcelable

@Parcelize
data class TextChatInfo(
    val msg: String
) : Parcelable

@Parcelize
data class CommonRePartyChatInfo(
    val msgNo: Long,
    val replyMsgNo: Int,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val partyNo: Int
) : Parcelable