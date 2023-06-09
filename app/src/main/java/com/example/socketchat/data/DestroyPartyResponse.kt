package com.example.socketchat.data

data class DestroyPartyResponse(
    val cmd: String,
    val errInfo: ErrorInfo,
    val data: Any?
) {
    data class ErrorInfo(
        val errNo: Int,
        val errMsg: String
    )
}