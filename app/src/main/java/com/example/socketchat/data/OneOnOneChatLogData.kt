package com.example.socketchat.data

import com.google.gson.annotations.SerializedName

data class OneOnOneChatLogData(
    @SerializedName("fromMemNo") val fromMemNo: Int,
    @SerializedName("toMemNo") val toMemNo: Int,
    @SerializedName("lastMsgNo") val lastMsgNo: Long,
    @SerializedName("countPerPage") val countPerPage: Int
)