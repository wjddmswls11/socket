package com.example.socketchat.adapter

import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.data.Re1On1ChatLog
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(
    private val currentUserMemNo: Int,
    private val summaryData: SummaryUserInfoResponse
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {


    private val oneOnOneChatList: ArrayList<Nt1On1TextChat> = arrayListOf()

    //새로운 채팅 메시지를 받아와 리스트에 추가합니다.
    fun updateChatData(newChat: Nt1On1TextChat) {
        oneOnOneChatList.add(newChat)
        notifyItemInserted(oneOnOneChatList.size - 1)

    }

    //이전 대화 내용의 역순으로 채팅을 표시하기 위해 새로운 채팅 메시지를 리스트의 맨 앞에 추가합니다.
    fun addChatDataAtFront(newData: Nt1On1TextChat) {
        oneOnOneChatList.add(0, newData)
        notifyItemInserted(0)
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
        holder.bind(oneOnOne)
    }

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(oneOnOneChatData: Nt1On1TextChat) {
            val message = oneOnOneChatData.data.textChatInfo.msg
            val fromMemNo = oneOnOneChatData.data.commonRe1On1ChatInfo.fromMemNo
            val msgNo = oneOnOneChatData.data.commonRe1On1ChatInfo.msgNo
            val time = convertTimestampToTime(msgNo)
            Log.d(
                "ChatListAdapter",
                "Message: $message, fromMemNo: $fromMemNo, currentUserMemNo: $currentUserMemNo msgNo: $msgNo time: $time"
            )

            Glide.with(itemView)
                .load(summaryData.data.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgChatMessageLeft)


            if (oneOnOneChatData.data.commonRe1On1ChatInfo.fromMemNo == currentUserMemNo) {
                binding.ctlChatRight.visibility = View.VISIBLE
                binding.ctlChatLeft.visibility = View.GONE
                binding.messageTextViewRight.text = message
                binding.messageTextViewReadRight.text = time
            } else {
                binding.ctlChatLeft.visibility = View.VISIBLE
                binding.ctlChatRight.visibility = View.GONE
                binding.messageTextViewLeft.text = message
                binding.messageTextViewReadLeft.text = time
            }
        }
    }
}