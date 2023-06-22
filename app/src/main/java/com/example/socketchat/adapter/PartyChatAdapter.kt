package com.example.socketchat.adapter

import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.PartyChatActivity
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.ItemPartyChatSelectBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PartyChatAdapter(
    private val currentUserMemNo: Int,
    private val partyMemberList: ArrayList<RePartyMemberListResponse>,
    private val activity: PartyChatActivity
) : RecyclerView.Adapter<PartyChatAdapter.PartyChatHolder>() {

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


    init {
        Log.d("PartyChatAdapter", "currentUserMemNo: $currentUserMemNo")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyChatHolder {
        val binding =
            ItemPartyChatSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PartyChatHolder(binding)
    }

    override fun getItemCount(): Int {
        return partyChatList.size
    }

    override fun onBindViewHolder(holder: PartyChatHolder, position: Int) {
        val partyChat = partyChatList[position]
        holder.bind(partyChat)
    }


    inner class PartyChatHolder(private val binding: ItemPartyChatSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ntUserJoinedPartyResponse: PartyChatResponse?) {
            val message = ntUserJoinedPartyResponse?.data?.textChatInfo?.msg
            val fromMemNo = ntUserJoinedPartyResponse?.data?.commonRePartyChatInfo?.fromMemNo
            val msgNo = ntUserJoinedPartyResponse?.data?.commonRePartyChatInfo?.msgNo
            Log.d(
                "ChatListAdapter",
                "Message: $message, fromMemNo: $fromMemNo, currentUserMemNo: $currentUserMemNo msgNo: $msgNo"
            )
            val time = msgNo?.let { it -> convertTimestampToTime(it) }

            var memberProfileUrl: String? = null
            var memberNickName: String? = null
            for (member in partyMemberList) {
                for (data in member.data) {
                    if (data.memNo == fromMemNo) {
                        memberProfileUrl = data.mainProfileUrl
                        memberNickName = data.nickName
                        break
                    }
                }
                if (memberProfileUrl != null) {
                    break
                }
            }

            Glide.with(activity)
                .load(memberProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgPartyChatAnother)

            binding.txtPartyChatNameLeft.text = memberNickName

            if (ntUserJoinedPartyResponse?.data?.commonRePartyChatInfo?.fromMemNo == currentUserMemNo && ntUserJoinedPartyResponse.cmd == "RePartyTextChat") {
                binding.ctlPartyChatRight.visibility = View.VISIBLE
                binding.ctlPartyChatLeft.visibility = View.GONE
                binding.ctlPartyChatJoined.visibility = View.GONE
                binding.messageTextViewPartyRight.text = message
                binding.messageTextViewTimeRight.text = time
            } else if (ntUserJoinedPartyResponse?.cmd == "NtPartyTextChat") {
                binding.ctlPartyChatLeft.visibility = View.VISIBLE
                binding.ctlPartyChatRight.visibility = View.GONE
                binding.ctlPartyChatJoined.visibility = View.GONE
                binding.messageTextViewPartyLeft.text = message
                binding.messageTextViewTimeLeft.text = time
            } else if(ntUserJoinedPartyResponse?.cmd == "NtUserJoinedParty") {
                binding.ctlPartyChatJoined.visibility = View.VISIBLE
                binding.ctlPartyChatRight.visibility = View.GONE
                binding.ctlPartyChatLeft.visibility = View.GONE
                binding.txtPartyChatJoinedNickname.text = ntUserJoinedPartyResponse.data.joinUserInfo.nickName
            }
        }
    }
}