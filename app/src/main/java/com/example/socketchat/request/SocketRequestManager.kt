package com.example.socketchat.request

import android.util.Log
import com.example.socketchat.socket.SocketManager
import org.json.JSONObject

class SocketRequestManager {

    private val socketManager = SocketManager

    private val eventLobby = "Lobby"
    private val eventParty = "Party"

    //파티입장
    fun sendJoinPartyRequest(partyNo : Int, ownerMemNo : Int, rqMemNo : Int) {
        val requestData = JSONObject().apply {
            put("cmd","RqJoinParty")
            put("data", JSONObject().apply {
                put("partyNo", partyNo)
                put("ownerMemNo", ownerMemNo)
                put("rqMemNo", rqMemNo)
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit(eventLobby, requestData)
    }

    //파티장 파티참여 수락
    fun sendAcceptParty(partyNo : Int, isAccept: Boolean, ownerMemNo: Int, rqMemNo: Int) {
        val requestData = JSONObject().apply {
            put("cmd","RqAcceptParty")
            put("data",JSONObject().apply {
                put("isAccept", isAccept)
                put("rqJoinParty", JSONObject().apply {
                    put("partyNo", partyNo)
                    put("ownerMemNo", ownerMemNo)
                    put("rqMemNo", rqMemNo)
                })
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit(eventLobby, requestData)
    }


    //유저강퇴
    fun sendKickOutUser(partyNo : Int, ownerMemNo : Int, kickoutMemNo : Int){
        val requestData = JSONObject().apply {
            put("cmd","RqKickoutUser")
            put("data", JSONObject().apply {
                put("partyNo", partyNo)
                put("ownerMemNo", ownerMemNo)
                put("kickoutMemNo", kickoutMemNo)
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit(eventParty, requestData)
    }

    //1:1채팅 삭제
    fun sendDeleteOneOnOneChat(delMsgNo : Long, fromMemNo : Int, toMemNo : Int) {
        val requestData = JSONObject().apply {
            put("cmd","RqDelete1On1Chat")
            put("data",JSONObject().apply {
                put("delMsgNo", delMsgNo)
                put("fromMemNo", fromMemNo)
                put("toMemNo", toMemNo)
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket?.emit(eventLobby, requestData)
    }
}