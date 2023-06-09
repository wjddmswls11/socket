package com.example.socketchat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.databinding.ItemPartyJoinDialogBinding
import com.example.socketchat.request.SocketRequestManager

class PartyJoinAdapter(private val currentUserMemNo: Int) : RecyclerView.Adapter<PartyJoinAdapter.PartyJoinViewHolder>(){

    private var partyJoinLIst : ArrayList<NtRequestJoinPartyResponse> = arrayListOf()
    private val socketRequestManager = SocketRequestManager()

    var onAcceptClick : (() -> Unit)? = null
    var onCancleClick : (() -> Unit)? = null

    fun setData(newData : ArrayList<NtRequestJoinPartyResponse>){
        partyJoinLIst = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyJoinViewHolder {
        val binding = ItemPartyJoinDialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        binding.btnPartyJoinDialogAccept.setOnClickListener {
            val partyNo = partyJoinLIst[0].data.SummaryPartyInfo.partyNo
            val ownerMemNo = partyJoinLIst[0].data.SummaryPartyInfo.memNo
            val rqMemNo = partyJoinLIst[0].data.RqUserInfo.memNo

            socketRequestManager.sendAcceptParty(partyNo, true, ownerMemNo, rqMemNo)

            onAcceptClick?.let { it() }
        }

        binding.btnPartyJoinDialogCancel.setOnClickListener {
            val partyNo = partyJoinLIst[0].data.SummaryPartyInfo.partyNo
            val ownerMemNo = partyJoinLIst[0].data.SummaryPartyInfo.memNo
            val rqMemNo = partyJoinLIst[0].data.RqUserInfo.memNo

            socketRequestManager.sendAcceptParty(partyNo, false, ownerMemNo, rqMemNo)

            onCancleClick?.let { it() }
        }

        return PartyJoinViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return partyJoinLIst.size
    }

    override fun onBindViewHolder(holder: PartyJoinViewHolder, position: Int) {
        val partyJoin = partyJoinLIst[position]
        holder.bind(partyJoin)
    }


    inner class PartyJoinViewHolder(private val binding : ItemPartyJoinDialogBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(ntRequestJoinPartyResponse : NtRequestJoinPartyResponse) {
            val partyNo = ntRequestJoinPartyResponse.data.SummaryPartyInfo.partyNo
            val memNo = ntRequestJoinPartyResponse.data.RqUserInfo.memNo
            val curMember = ntRequestJoinPartyResponse.data.SummaryPartyInfo.curMemberCount
            val maxMember = ntRequestJoinPartyResponse.data.SummaryPartyInfo.maxMemberCount

            binding.partyJoinDialogPartyNo.text = partyNo.toString()
            binding.partyJoinDialogMemNO.text = memNo.toString()
            binding.partyJoinDialogCurMemberCount.text = curMember.toString()
            binding.partyJoinDialogMaxMemberCount.text = maxMember.toString()


            // Output currentUserMemNo to log
            Log.d("PartyJoinAdapter", "currentUserMemNo: $currentUserMemNo")

            Log.d("PartyJoinAdapter", "partyNo: $partyNo, memNo: $memNo, curMember: $curMember, maxMember: $maxMember")

        }
    }

}