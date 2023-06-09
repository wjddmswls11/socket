package com.example.socketchat.adapter

import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.data.Party
import com.example.socketchat.data.PartyChatResponse
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.ItemPartyChatSelectBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PartyChatAdapter(
    private val currentUserMemNo: Int,
    private val partyData: Party,
    private val partyMemberList: ArrayList<RePartyMemberListResponse>
) : RecyclerView.Adapter<PartyChatAdapter.PartyChatHolder>() {

    private val partyChatList: ArrayList<PartyChatResponse> = arrayListOf()

    fun updatePartyChatDataList(newDataList: PartyChatResponse) {
        var isAlreadyExists = false
        val newMessageNo = newDataList.data.commonRePartyChatInfo?.msgNo
        for (chat in partyChatList) {
            val oldMessageNo = chat.data.commonRePartyChatInfo?.msgNo
            if (oldMessageNo == newMessageNo) {
                isAlreadyExists = true
                break
            }
        }
        if (!isAlreadyExists) {
            partyChatList.add(newDataList)
            notifyItemInserted(partyChatList.size -1)
        }
    }

    fun addPartyChatDataAtFront(newData : PartyChatResponse) {
        partyChatList.add(0, newData)
        notifyItemInserted(0)
    }

    private fun convertTimestampToTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }




    init {
        Log.d("PartyChatAdapter", "currentUserMemNo: $currentUserMemNo")
        Log.d("PartyChatAdapter", "partyData: $partyData")
        Log.d("PartyChatAdapter", "partyMemberList: $partyMemberList")
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
        fun bind(ntUserJoinedPartyResponse: PartyChatResponse) {
            val message = ntUserJoinedPartyResponse.data.textChatInfo?.msg ?: ""
            val fromMemNo = ntUserJoinedPartyResponse.data.commonRePartyChatInfo?.fromMemNo ?: -1

            val msgNo = ntUserJoinedPartyResponse.data.commonRePartyChatInfo.msgNo
            Log.d("ChatListAdapter", "Message: $message, fromMemNo: $fromMemNo, currentUserMemNo: $currentUserMemNo msgNo: $msgNo")
            val time = convertTimestampToTime(msgNo)

            var memberProfileUrl : String? = null
            var memberNickName : String? = null
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



            Glide.with(binding.root)
                .load(memberProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgPartyChatAnother)

            binding.txtPartyChatNameLeft.text = memberNickName

            if (fromMemNo != -1) {
                if (ntUserJoinedPartyResponse.data.commonRePartyChatInfo.fromMemNo == currentUserMemNo){
                    binding.ctlPartyChatRight.visibility = View.VISIBLE
                    binding.ctlPartyChatLeft.visibility = View.GONE
                    binding.messageTextViewPartyRight.text = message
                    binding.messageTextViewTimeRight.text = time
                } else {
                    binding.ctlPartyChatLeft.visibility = View.VISIBLE
                    binding.ctlPartyChatRight.visibility = View.GONE
                    binding.messageTextViewPartyLeft.text = message
                    binding.messageTextViewTimeLeft.text = time
                }

            }else {
                binding.ctlPartyChatRight.visibility = View.GONE
                binding.ctlPartyChatLeft.visibility = View.GONE
            }


        }
    }

}