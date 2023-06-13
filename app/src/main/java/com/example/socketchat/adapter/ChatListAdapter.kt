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
import com.example.socketchat.data.Nt1On1TextChat
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ItemChatMessageBinding
import com.example.socketchat.request.SocketRequestManager
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(
    private val currentUserMemNo: Int,
    private val summaryData: SummaryUserInfoResponse
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {


    private val oneOnOneChatList: ArrayList<Nt1On1TextChat> = arrayListOf()
    private val socketRequestManager = SocketRequestManager()

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

            Glide.with(itemView)
                .load(summaryData.data.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgChatMessageLeft)


            if (oneOnOneChatData?.data?.commonRe1On1ChatInfo?.fromMemNo == currentUserMemNo) {
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

            // "삭제하시겠습니까?" 다이얼로그를 보여주는 함수
            binding.ctlChatRight.setOnLongClickListener {
                val chatData = oneOnOneChatList[position]
                val delMsgNo = chatData.data.commonRe1On1ChatInfo.msgNo
                val fromMemNo = chatData.data.commonRe1On1ChatInfo.fromMemNo
                val toMemNo = chatData.data.commonRe1On1ChatInfo.toMemNo

                Log.d(
                    "ChatListAdapter 삭제",
                    "delMsgNo: $delMsgNo, fromMemNo: $fromMemNo, toMemNo: $toMemNo"
                )



                AlertDialog.Builder(itemView.context)
                    .setMessage("삭제하시겠습니까?")
                    .setPositiveButton("네") { _, _ ->
                        socketRequestManager.sendDeleteOneOnOneChat(delMsgNo, fromMemNo, toMemNo)
                    }
                    .setNegativeButton("아니요", null)
                    .show()
                true
            }
        }

    }
}