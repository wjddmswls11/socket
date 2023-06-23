package com.example.socketchat.adapter

import android.app.AlertDialog
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.OneOnOneChatActivity
import com.example.socketchat.R
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ItemChatMessageBinding
import com.example.socketchat.request.SocketRequestManager
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(
    private val currentUserMemNo: Int,
    private val summaryData: SummaryUserInfoResponse,
    private val activity : OneOnOneChatActivity
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {


    private val oneOnOneChatList: ArrayList<Nt1On1TextChat> = arrayListOf()
    private val socketRequestManager = SocketRequestManager()



    //삭제된 채팅 메시지를 처리하는 함수
    fun deleteChatData(deleteChat : Long) {
        val position = oneOnOneChatList.indexOfFirst { it.data.commonRe1On1ChatInfo.msgNo == deleteChat }
        if (position != -1) {
            val chatItem = oneOnOneChatList[position]
            //현재 시간에서 채팅 메시지의 msgNo(시간)을 뺀 값을 분 단위로 계산합니다.
            val timeDifferenceMinutes =
                (System.currentTimeMillis() - chatItem.data.commonRe1On1ChatInfo.msgNo) / 60000

            //시간 차이가 5분 이내인 경우에만 채팅 메시지를 삭제합니다.(ui의 업데이트를 위해서 copy를 사용)
            if (timeDifferenceMinutes <= 5) {
                val newData = chatItem.data.copy(
                    commonRe1On1ChatInfo = chatItem.data.commonRe1On1ChatInfo.copy(isDeleted = true)
                )

                val newChatItem = chatItem.copy(data = newData)
                oneOnOneChatList[position] = newChatItem
                notifyItemChanged(position)
            }
        }
    }




    //새로운 채팅 메시지를 받아와 리스트에 추가합니다.
    fun updateChatData(newChat: Nt1On1TextChat) {
        oneOnOneChatList.add(newChat)
        notifyItemInserted(oneOnOneChatList.size - 1)
    }

    //이전 대화 내용의 역순으로 채팅을 표시하기 위해 새로운 채팅 메시지를 리스트의 맨 앞에 추가합니다.
    fun addChatDataAtFront(newData: List<Nt1On1TextChat>) {
        oneOnOneChatList.addAll(0, newData)
        notifyItemRangeInserted(0, newData.size)
    }



    //타임 스탬프를 시간 형식으로 변환하는 함수입니다.
    fun convertTimestampToTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return oneOnOneChatList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val oneOnOne = oneOnOneChatList[position]
        holder.bind(oneOnOne, position)
    }

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?, position: Int) {
            val message = oneOnOneChatData?.data?.textChatInfo?.msg
            val fromMemNo = oneOnOneChatData?.data?.commonRe1On1ChatInfo?.fromMemNo
            val msgNo = oneOnOneChatData?.data?.commonRe1On1ChatInfo?.msgNo
            val time = msgNo?.let { it -> convertTimestampToTime(it) }
            Log.d(
                "ChatListAdapter",
                "Message: $message, fromMemNo: $fromMemNo, currentUserMemNo: $currentUserMemNo msgNo: $msgNo time: $time"
            )

            Glide.with(activity)
                .load(summaryData.data.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgChatMessageLeft)


            if (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.fromMemNo == currentUserMemNo && oneOnOneChatData.cmd == "Re1On1TextChat") {
                binding.ctlChatRight.visibility = View.VISIBLE
                binding.ctlChatLeft.visibility = View.GONE
                binding.ctlChatJoin.visibility = View.GONE
                binding.ctlChatRejoin.visibility = View.GONE
                binding.viewChatRejoin.visibility = View.GONE
                binding.messageTextViewRight.text = message
                binding.messageTextViewReadRight.text = time

            } else if (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.toMemNo == currentUserMemNo && oneOnOneChatData.cmd == "Nt1On1TextChat"){
                binding.ctlChatLeft.visibility = View.VISIBLE
                binding.ctlChatRight.visibility = View.GONE
                binding.ctlChatJoin.visibility = View.GONE
                binding.ctlChatRejoin.visibility = View.GONE
                binding.viewChatRejoin.visibility = View.GONE
                binding.messageTextViewLeft.text = message
                binding.messageTextViewReadLeft.text = time
            } else if(oneOnOneChatData?.cmd == "ReJoinParty") {
                binding.ctlChatRejoin.visibility = View.VISIBLE
                binding.ctlChatJoin.visibility = View.GONE
                binding.ctlChatLeft.visibility = View.GONE
                binding.ctlChatRight.visibility = View.GONE
                binding.viewChatRejoin.visibility = View.GONE
                binding.messageTextViewRejoinChatFirst.text = oneOnOneChatData.data.rqJoinParty.partyNo.toString()
                binding.messageTextViewRejoinCHatThird.text = oneOnOneChatData.data.commonRe1On1ChatInfo.replyMsgNo.toString()
            } else if (oneOnOneChatData?.cmd == "NtRequestJoinParty") {
                binding.ctlChatJoin.visibility = View.VISIBLE
                binding.ctlChatRejoin.visibility = View.GONE
                binding.ctlChatLeft.visibility = View.GONE
                binding.ctlChatRight.visibility = View.GONE
                binding.viewChatRejoin.visibility = View.GONE
                binding.messageTextViewChatNickName.text = oneOnOneChatData.data.rqUserInfo.nickName
                binding.messageTextViewChatPartyNo.text = oneOnOneChatData.data.summaryPartyInfo.partyNo.toString()
            }

            //숫자에 따라 다른 텍스트 표시
            when(oneOnOneChatData?.data?.commonRe1On1ChatInfo?.replyMsgNo) {
                0 -> binding.messageTextViewRejoinCHatThird.text = "신청되었습니다"
                1 -> binding.messageTextViewRejoinCHatThird.text = "방장이 거절했습니다"
                2 -> binding.messageTextViewRejoinCHatThird.text = "빈방이 없습니다"
                3 -> binding.messageTextViewRejoinCHatThird.text = "방이 꽉 찼습니다"
                4 -> binding.messageTextViewRejoinCHatThird.text = "이미 참석해 있습니다"
                5 -> binding.messageTextViewRejoinCHatThird.text = "이미 참석 신청을 해놓았습니다"
                6 -> binding.messageTextViewRejoinCHatThird.text = "강퇴 유저입니다"
                7 -> binding.messageTextViewRejoinCHatThird.text = "방장 승락대기중입니다"
                else -> binding.messageTextViewRejoinCHatThird.text = "기타"
            }



            if (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.isDeleted == true) {
                binding.messageTextViewRight.text = " 삭제된 메시지입니다."
                binding.messageTextViewRight.setTextColor(itemView.context.getColor(R.color.gray_bottom))
                binding.messageTextViewRight.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_report_24,
                    0,
                    0,
                    0
                )
                binding.messageTextViewLeft.text =" 삭제된 메시지입니다."
                binding.messageTextViewLeft.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_report_blue,0,0,0
                )

            } else {
                binding.messageTextViewRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding.messageTextViewLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            // "삭제하시겠습니까?" 다이얼로그를 보여주는 함수
            if (!oneOnOneChatList[position].data.commonRe1On1ChatInfo.isDeleted) {
                binding.ctlChatRight.setOnLongClickListener {
                    val chatData = oneOnOneChatList[position]
                    val delMsgNo = chatData.data.commonRe1On1ChatInfo.msgNo
                    val fromMemNoLongClick = chatData.data.commonRe1On1ChatInfo.fromMemNo
                    val toMemNo = chatData.data.commonRe1On1ChatInfo.toMemNo

                    Log.d(
                        "ChatListAdapter 삭제",
                        "delMsgNo: $delMsgNo, fromMemNo: $fromMemNoLongClick, toMemNo: $toMemNo"
                    )



                    AlertDialog.Builder(itemView.context)
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("네") { _, _ ->
                            socketRequestManager.sendDeleteOneOnOneChat(delMsgNo, fromMemNoLongClick, toMemNo)
                        }
                        .setNegativeButton("아니요", null)
                        .show()
                    true
                }
            }
        }
    }

}