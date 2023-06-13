package com.example.socketchat.data

data class PartyChatLogData(
    val partyNo: Int,
    val rqMemNo: Int,
    val lastMsgNo: Long,
    val countPerPage: Int,
    val sortType : Boolean
)