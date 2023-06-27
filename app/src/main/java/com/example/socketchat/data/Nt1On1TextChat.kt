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

data class OneOnOneDataErrInfo(
    val errNo: Int,
    val errMsg: String
) : Serializable

data class OneOnOneDataData(
    val textChatInfo: OneOnOneData,
    val commonRe1On1ChatInfo: CommonChatInfo,
    val isAccept : Boolean,
    val denyReason : Int,
    val rqJoinParty : JoinPartyNt1On1TextChat,
    val summaryPartyInfo : SummaryPartyInfoNt1On1TextChat,
    val rqUserInfo: RqUserInfoNt1On1TextChat
) : Serializable

data class OneOnOneData(
    val msg: String
) : Serializable


data class JoinPartyNt1On1TextChat(
    val partyNo: Int,
    val ownerMemNo: Int,
    val rqMemNo: Int
)

data class SummaryPartyInfoNt1On1TextChat(
    val partyNo: Int,
    val memNo: Int,
    val mainPhotoUrl: String,
    val title: String,
    val location: String,
    val curMemberCount: Int,
    val maxMemberCount: Int,
    val startTime: Long,
    val endTime: Long,
    val isAutoJoin: Boolean,
    val createAt: Long
) : Serializable

data class RqUserInfoNt1On1TextChat(
    val memNo: Int,
    val nickName: String,
    val mainProfileUrl: String
) : Serializable

data class CommonChatInfo(
    val msgNo: Long,
    val replyMsgNo: Long,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val toMemNo: Int
) : Serializable




