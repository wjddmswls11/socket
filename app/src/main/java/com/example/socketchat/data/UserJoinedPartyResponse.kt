package com.example.socketchat.data

data class UserJoinedPartyResponse(
    val cmd: String,
    val errInfo: ErrInfoUserJoined,
    val data: UserJoinedPartyData
)

data class ErrInfoUserJoined(
    val errNo: Int,
    val errMsg: String
)

data class UserJoinedPartyData(
    val joinUserInfo: JoinUserInfo,
    val commonRePartyChatInfo: CommonRePartyChatInfoUserJoined
)

data class JoinUserInfo(
    val memNo: Int,
    val nickName: String,
    val mainProfileUrl: String
)

data class CommonRePartyChatInfoUserJoined(
    val msgNo: Long,
    val replyMsgNo: Long,
    val remainReadCount: Int,
    val isDeleted: Boolean,
    val fromMemNo: Int,
    val partyNo: Int
)

