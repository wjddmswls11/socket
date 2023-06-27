package com.example.socketchat.adapter

import android.app.AlertDialog
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.R
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.databinding.ItemOneononechatLeftBinding
import com.example.socketchat.databinding.ItemOneononechatRightBinding
import com.example.socketchat.databinding.ItemOneononechatjoinBinding
import com.example.socketchat.databinding.ItemOneononechatrejoinBinding
import com.example.socketchat.viewmodel.OneOnOneViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OneOnOneChatAdapter(
    private val currentUserMemNo: Int, private val oneOnOneViewModel: OneOnOneViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CHAT_LEFT = 1
        private const val VIEW_TYPE_CHAT_RIGHT = 2
        private const val VIEW_TYPE_CHAT_JOIN = 3
        private const val VIEW_TYPE_CHAT_REJOIN = 4
    }

    private val oneOnOneChatList: ArrayList<Nt1On1TextChat> = arrayListOf()


    //삭제된 채팅 메시지를 처리하는 함수
    fun deleteChatData(deleteChat: Long) {
        val position =
            oneOnOneChatList.indexOfFirst { it.data.commonRe1On1ChatInfo.msgNo == deleteChat }
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CHAT_LEFT -> {
                val binding = ItemOneononechatLeftBinding.inflate(inflater, parent, false)
                OneOnOneChatLeftViewHolder(binding)
            }

            VIEW_TYPE_CHAT_RIGHT -> {
                val binding = ItemOneononechatRightBinding.inflate(inflater, parent, false)
                OneOnOneChatRightViewHolder(binding)
            }

            VIEW_TYPE_CHAT_JOIN -> {
                val binding = ItemOneononechatjoinBinding.inflate(inflater, parent, false)
                OneOnOneChatJoinViewHolder(binding)
            }

            VIEW_TYPE_CHAT_REJOIN -> {
                val binding = ItemOneononechatrejoinBinding.inflate(inflater, parent, false)
                OneOnOneChatReJoinViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun getItemCount(): Int {
        return oneOnOneChatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < oneOnOneChatList.size) {
            val oneOnOne = oneOnOneChatList[position]
            when (holder) {
                is OneOnOneChatLeftViewHolder -> holder.bind(oneOnOne)
                is OneOnOneChatRightViewHolder -> holder.bind(oneOnOne, position)
                is OneOnOneChatJoinViewHolder -> holder.bind(oneOnOne)
                is OneOnOneChatReJoinViewHolder -> holder.bind(oneOnOne)
                else -> throw IllegalArgumentException("Invalid view holder")
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val chatData = oneOnOneChatList[position]
        return when {
            chatData.data.commonRe1On1ChatInfo.fromMemNo == currentUserMemNo && chatData.cmd == "Re1On1TextChat" -> VIEW_TYPE_CHAT_RIGHT
            chatData.data.commonRe1On1ChatInfo.toMemNo == currentUserMemNo && chatData.cmd == "Nt1On1TextChat" -> VIEW_TYPE_CHAT_LEFT
            chatData.cmd == "ReJoinParty" -> VIEW_TYPE_CHAT_REJOIN
            chatData.cmd == "NtRequestJoinParty" -> VIEW_TYPE_CHAT_JOIN
            else -> throw IllegalArgumentException("Invalid item view type")
        }
    }


    inner class OneOnOneChatLeftViewHolder(private val binding: ItemOneononechatLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?) {
            val message = oneOnOneChatData?.data?.textChatInfo?.msg
            val msgNo = oneOnOneChatData?.data?.commonRe1On1ChatInfo?.msgNo
            val time = msgNo?.let { it -> convertTimestampToTime(it) }


            binding.messageTextViewLeft.text = message
            binding.messageTextViewReadLeft.text = time

            if (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.isDeleted == true) {
                binding.messageTextViewLeft.text = " 삭제된 메시지입니다."
                binding.messageTextViewLeft.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_report_blue, 0, 0, 0
                )
            } else {
                binding.messageTextViewLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    inner class OneOnOneChatRightViewHolder(private val binding: ItemOneononechatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?, position: Int) {
            val message = oneOnOneChatData?.data?.textChatInfo?.msg
            val msgNo = oneOnOneChatData?.data?.commonRe1On1ChatInfo?.msgNo
            val time = msgNo?.let { it -> convertTimestampToTime(it) }

            binding.messageTextViewRight.text = message
            binding.messageTextViewReadRight.text = time

            if (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.isDeleted == true) {
                binding.messageTextViewRight.text = " 삭제된 메시지입니다."
                binding.messageTextViewRight.setTextColor(itemView.context.getColor(R.color.gray_bottom))
                binding.messageTextViewRight.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_report_24,
                    0,
                    0,
                    0
                )
            } else {
                binding.messageTextViewRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
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
                            oneOnOneViewModel.sendDeleteOneOnOneChat(
                                delMsgNo,
                                fromMemNoLongClick,
                                toMemNo
                            )
                        }
                        .setNegativeButton("아니요", null)
                        .show()
                    true
                }
            }
        }
    }

    inner class OneOnOneChatJoinViewHolder(private val binding: ItemOneononechatjoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?) {
            binding.messageTextViewChatNickName.text =
                oneOnOneChatData?.data?.rqUserInfo?.nickName
            binding.messageTextViewChatPartyNo.text =
                oneOnOneChatData?.data?.summaryPartyInfo?.partyNo.toString()
        }
    }

    inner class OneOnOneChatReJoinViewHolder(private val binding: ItemOneononechatrejoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?) {
            binding.messageTextViewRejoinChatFirst.text =
                oneOnOneChatData?.data?.rqJoinParty?.partyNo.toString()
            binding.messageTextViewRejoinCHatThird.text =
                oneOnOneChatData?.data?.commonRe1On1ChatInfo?.replyMsgNo.toString()

            //숫자에 따라 다른 텍스트 표시
            when (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.replyMsgNo?.toInt()) {
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
        }
    }
}