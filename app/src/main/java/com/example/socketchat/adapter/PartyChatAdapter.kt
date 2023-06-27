package com.example.socketchat.adapter

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.ItemPartychatJoinedBinding
import com.example.socketchat.databinding.ItemPartychatLeavedBinding
import com.example.socketchat.databinding.ItemPartychatLeftBinding
import com.example.socketchat.databinding.ItemPartychatRightBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PartyChatAdapter(
    private val partyMemberList: ArrayList<RePartyMemberListResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CHAT_LEFT = 1
        private const val VIEW_TYPE_CHAT_RIGHT = 2
        private const val VIEW_TYPE_CHAT_JOINED = 3
        private const val VIEW_TYPE_CHAT_LEAVED = 4
    }


    private val partyChatList: ArrayList<PartyChatResponse> = arrayListOf()

    fun updatePartyChatDataList(newDataList: PartyChatResponse) {
        partyChatList.add(newDataList)
        notifyItemInserted(partyChatList.size - 1)
    }

    fun addPartyChatDataAtFront(newData: List<PartyChatResponse>) {
        partyChatList.addAll(0, newData)
        notifyItemRangeInserted(0, newData.size)
    }

    private fun convertTimestampToTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CHAT_LEFT -> {
                val binding = ItemPartychatLeftBinding.inflate(inflater, parent, false)
                PartyChatLeftViewHolder(binding)
            }

            VIEW_TYPE_CHAT_RIGHT -> {
                val binding = ItemPartychatRightBinding.inflate(inflater, parent, false)
                PartyChatRightViewHolder(binding)
            }

            VIEW_TYPE_CHAT_JOINED -> {
                val binding = ItemPartychatJoinedBinding.inflate(inflater, parent, false)
                PartyChatJoinedViewHolder(binding)
            }

            VIEW_TYPE_CHAT_LEAVED -> {
                val binding = ItemPartychatLeavedBinding.inflate(inflater, parent, false)
                PartyChatLeavedViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun getItemCount(): Int {
        return partyChatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val partyChat = partyChatList[position]
        when (holder) {
            is PartyChatLeftViewHolder -> holder.bind(partyChat)
            is PartyChatRightViewHolder -> holder.bind(partyChat)
            is PartyChatJoinedViewHolder -> holder.bind(partyChat)
            is PartyChatLeavedViewHolder -> holder.bind(partyChat)
            else -> throw IllegalArgumentException("Invalid view holder")
        }
    }


    override fun getItemViewType(position: Int): Int {
        val partyChat = partyChatList[position]
        return when (partyChat.cmd) {
            "NtPartyTextChat" -> VIEW_TYPE_CHAT_LEFT
            "RePartyTextChat" -> VIEW_TYPE_CHAT_RIGHT
            "NtUserJoinedParty" -> VIEW_TYPE_CHAT_JOINED
            "NtUserLeavedParty" -> VIEW_TYPE_CHAT_LEAVED
            else -> throw IllegalArgumentException("유효하지 않은 아이템 뷰 타입입니다: ${partyChat.cmd}")
        }
    }


    inner class PartyChatLeftViewHolder(private val binding: ItemPartychatLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(partyChat: PartyChatResponse?) {
            val message = partyChat?.data?.textChatInfo?.msg
            val time =
                partyChat?.data?.commonRePartyChatInfo?.msgNo?.let { convertTimestampToTime(it) }
            binding.messageTextViewPartyLeft.text = message
            binding.messageTextViewTimeLeft.text = time

            val fromMemNo = partyChat?.data?.commonRePartyChatInfo?.fromMemNo
            val member = partyMemberList.flatMap { it.data }.find { it.memNo == fromMemNo }

            if (member != null) {
                Glide.with(binding.root)
                    .load(member.mainProfileUrl)
                    .transform(CircleCrop())
                    .into(binding.imgPartyChatAnother)

                binding.txtPartyChatNameLeft.text = member.nickName
            }
        }
    }


    inner class PartyChatRightViewHolder(private val binding: ItemPartychatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(partyChat: PartyChatResponse?) {
            val message = partyChat?.data?.textChatInfo?.msg
            val time =
                partyChat?.data?.commonRePartyChatInfo?.msgNo?.let { convertTimestampToTime(it) }
            binding.messageTextViewPartyRight.text = message
            binding.messageTextViewTimeRight.text = time
        }
    }


    inner class PartyChatJoinedViewHolder(private val binding: ItemPartychatJoinedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(partyChat: PartyChatResponse?) {
            binding.txtPartyChatJoinedNickname.text = partyChat?.data?.joinUserInfo?.nickName
        }
    }


    inner class PartyChatLeavedViewHolder(private val binding: ItemPartychatLeavedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(partyChat: PartyChatResponse?) {
            val memNo = partyChat?.data?.memNo
            val member = partyMemberList.flatMap { it.data }.find { it.memNo == memNo }
            if (member != null) {
                binding.txtPartyChatLeavedNickname.text = member.nickName
            }
        }
    }


}