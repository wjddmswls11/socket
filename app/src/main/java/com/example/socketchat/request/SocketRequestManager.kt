package com.example.socketchat.request

import android.util.Log
import com.example.socketchat.socket.SocketManager
import org.json.JSONObject

class SocketRequestManager {

    private val socketManager = SocketManager



    //회원인증
    fun sendAuthRequest(memNo: Int) {
        val requestData = JSONObject().apply {
            put("cmd", "RqAuthUser")
            put("data", JSONObject().apply {
                put("memNo", memNo)
            })
        }

        Log.d("Socket Request", "Sending RqAuthUser: $requestData")
        socketManager.socket.emit("Lobby", requestData)
    }



    //1:1채팅
    fun send1On1ChatRequest(msg: String, fromMemNo: Int, toMemNo: Int) {
        val requestData = JSONObject().apply {
            put("cmd", "Rq1On1TextChat")
            put("data", JSONObject().apply {
                put("textChatInfo", JSONObject().apply {
                    put("msg", msg)
                })
                put("commonRq1On1ChatInfo", JSONObject().apply {
                    put("replyMsgNo", 0)
                    put("fromMemNo", fromMemNo)
                    put("toMemNo", toMemNo)
                })
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket.emit("Lobby", requestData)

    }

    //파티입장
    fun senJoinPartyRequest(partyNo : Int, ownerMemNo : Int, rqMemNo : Int) {
        val requestData = JSONObject().apply {
            put("cmd","RqJoinParty")
            put("data", JSONObject().apply {
                put("partyNo", partyNo)
                put("ownerMemNo", ownerMemNo)
                put("rqMemNo", rqMemNo)
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket.emit("Lobby", requestData)
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
        socketManager.socket.emit("Lobby",requestData)
    }

    //파티채팅
    fun sendPartyChat(msg: String, fromMemNo : Int, partyNo : Int){
        val requestData = JSONObject().apply {
            put("cmd", "RqPartyTextChat")
            put("data", JSONObject().apply {
                put("textChatInfo", JSONObject().apply {
                    put("msg", msg)
                })
                put("commonRqPartyChatInfo", JSONObject().apply {
                    put("replyMsgNo", 0)
                    put("fromMemNo", fromMemNo)
                    put("partyNo", partyNo)
                })
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket.emit("Party", requestData)
    }

    //파티떠나기
    fun sendLeaveParty(partyNo : Int, memNo : Int) {
        val requestData = JSONObject().apply {
            put("cmd", "RqLeaveParty")
            put("data", JSONObject().apply {
                put("partyNo", partyNo)
                put("memNo",memNo)
            })
        }
        Log.d("SocketRequestManager", "Socket Request Data: $requestData")
        socketManager.socket.emit("Party", requestData)
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
        socketManager.socket.emit("Party", requestData)
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
        socketManager.socket.emit("Lobby", requestData)
    }
}