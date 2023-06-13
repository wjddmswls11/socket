package com.example.socketchat.data

data class DeleteOneOnOneResponseData(
    val cmd: String,
    val errInfo: ErrorInfoDeleteOneOnOne,
    val data: ChatDataDeleteOneOnOne
)

data class ErrorInfoDeleteOneOnOne(
    val errNo: Int,
    val errMsg: String
)

data class ChatDataDeleteOneOnOne(
    val rqDelete1On1Chat: DeleteInfoDeleteOneOnOne,
    val result: Int
)

data class DeleteInfoDeleteOneOnOne(
    val delMsgNo: Long,
    val fromMemNo: Int,
    val toMemNo: Int
)