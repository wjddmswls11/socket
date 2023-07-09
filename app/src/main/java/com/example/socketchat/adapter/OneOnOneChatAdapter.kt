package com.example.socketchat.adapter

import android.app.AlertDialog
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.OneOnOneChatActivity
import com.example.socketchat.R
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ItemOneononechatLeftBinding
import com.example.socketchat.databinding.ItemOneononechatRightBinding
import com.example.socketchat.databinding.ItemOneononechatjoinBinding
import com.example.socketchat.databinding.ItemOneononechatrejoinBinding
import com.example.socketchat.viewmodel.OneOnOneViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OneOnOneChatAdapter(
    private val currentUserMemNo: Int, private val oneOnOneViewModel: OneOnOneViewModel,private val activity: OneOnOneChatActivity,
    private val summaryData : SummaryUserInfoResponse
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
        fun bind(oneOnOneChat: Nt1On1TextChat?) {
            binding.oneOnOneChat = oneOnOneChat
            Glide.with(activity)
                .load(summaryData.data.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgChatMessageLeft)
        }
    }

    inner class OneOnOneChatRightViewHolder(private val binding: ItemOneononechatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?, position: Int) {
            binding.oneOnOneChat = oneOnOneChatData
            // "삭제하시겠습니까?" 다이얼로그를 보여주는 함수
            if (!oneOnOneChatList[position].data.commonRe1On1ChatInfo.isDeleted) {
                binding.ctlChatRight.setOnLongClickListener {
                    val chatData = oneOnOneChatList[position]
                    val delMsgNo = chatData.data.commonRe1On1ChatInfo.msgNo
                    val fromMemNoLongClick = chatData.data.commonRe1On1ChatInfo.fromMemNo
                    val toMemNo = chatData.data.commonRe1On1ChatInfo.toMemNo

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
            binding.oneOnOneChat = oneOnOneChatData
        }
    }

    inner class OneOnOneChatReJoinViewHolder(private val binding: ItemOneononechatrejoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat?) {
            binding.oneOnOneChat = oneOnOneChatData
        }
    }
}