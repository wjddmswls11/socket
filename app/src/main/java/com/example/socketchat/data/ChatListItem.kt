package com.example.socketchat.data

sealed class ChatListItem

data class OneOnOneChatListItem(val chat: Nt1On1TextChat) : ChatListItem()

data class JoinPartyListItem(val joinParty : NtRequestJoinPartyResponse) : ChatListItem()
