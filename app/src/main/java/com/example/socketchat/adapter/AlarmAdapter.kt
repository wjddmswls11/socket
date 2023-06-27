package com.example.socketchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.MenuActivity
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.databinding.ItemAlarmNtrequestBinding
import com.example.socketchat.viewmodel.MenuViewModel

class AlarmAdapter(private val activity : MenuActivity, private val menuViewModel : MenuViewModel) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private val alarmList : ArrayList<NtRequestJoinPartyResponse> = arrayListOf()

    fun updateData(newData : List<NtRequestJoinPartyResponse>) {
        alarmList.clear()
        alarmList.addAll(newData)
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmNtrequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarmList[position]
        holder.bind(alarm)

    }





    inner class AlarmViewHolder(private val binding: ItemAlarmNtrequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alarmList : NtRequestJoinPartyResponse) {
            Glide.with(activity)
                .load(alarmList.data.rqUserInfo.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgAlarmProfile)

            binding.messageTextViewChatNickName.text = alarmList.data.rqUserInfo.nickName

            binding.messageTextViewChatPartyNo.text = alarmList.data.summaryPartyInfo.partyNo.toString()

            binding.buttonChatLeft.setOnClickListener {
                val partyNo = alarmList.data.summaryPartyInfo.partyNo
                val ownerMemNo = alarmList.data.summaryPartyInfo.memNo
                val rqMemNo = alarmList.data.rqUserInfo.memNo
                menuViewModel.sendAcceptParty(partyNo,true, ownerMemNo, rqMemNo)

                //해당 아이템 제거
                removeItem(bindingAdapterPosition)

            }

            binding.buttonChatRight.setOnClickListener {
                val partyNo = alarmList.data.summaryPartyInfo.partyNo
                val ownerMemNo = alarmList.data.summaryPartyInfo.memNo
                val rqMemNo = alarmList.data.rqUserInfo.memNo
                menuViewModel.sendAcceptParty(partyNo, false, ownerMemNo, rqMemNo)

                // 해당 아이템 제거
                removeItem(bindingAdapterPosition)
            }

        }


        //position에 해당되는 아이템을 alarmList에서 제거하고, notifyItemRemoved 호출
        private fun removeItem(position: Int) {
            if (position in 0 until alarmList.size) {
                alarmList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }

}