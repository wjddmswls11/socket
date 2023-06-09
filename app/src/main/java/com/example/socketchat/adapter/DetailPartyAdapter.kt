package com.example.socketchat.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socketchat.R
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.data.MemberInfo
import com.example.socketchat.data.Party
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.ItemDetailPartyBinding
import com.example.socketchat.request.SocketRequestManager

class DetailPartyAdapter(private val context : Context) : RecyclerView.Adapter<DetailPartyAdapter.DetailViewHolder>(){

    private var detailList : ArrayList<MemberInfo> = arrayListOf()
    private var partyData : Party? = null
    private var currentUserMemNo: Int = -1
    private val socketRequestManager = SocketRequestManager()

    fun setData(newData : ArrayList<MemberInfo>){
        detailList.clear()
        detailList.addAll(newData)
        notifyDataSetChanged()
    }

    fun setPartyData(newPartyData : Party?) {
        partyData = newPartyData
    }

    fun setCurrentUserMemNo(newCurrentUserMemNo: Int) {
        currentUserMemNo = newCurrentUserMemNo
    }

    fun updateMemberList(memberList: ArrayList<MemberInfo>) {
        detailList.clear()
        detailList.addAll(memberList)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemDetailPartyBinding.inflate(LayoutInflater.from(parent.context),parent,false)


        return DetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detail = detailList[position]
        holder.bind(detail)
    }

    inner class DetailViewHolder(private val binding : ItemDetailPartyBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(detailMember : MemberInfo){

            Log.d("DetailPartyAdapter", "Party No: $partyData")
            Log.d("DetailPartyAdapter", "Party No: ${partyData?.partyNo}")
            Log.d("DetailPartyAdapter", "Current User Mem No: $currentUserMemNo")
            Log.d("DetailPartyAdapter", "detailList : $detailList")

            val partyNo = partyData?.partyNo
            val ownerMemNo = partyData?.memNo

            binding.btnDetailPartyList.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val kickoutMemNo = detailList[position].memNo

                    Log.d("DetailPartyAdapter", "Kicking out member: $kickoutMemNo")

                    if (partyNo != null && ownerMemNo != null) {
                        Log.d("DetailPartyAdapter", "Sending kick out request - Party No: $partyNo, Owner Mem No: $ownerMemNo, Kickout Mem No: $kickoutMemNo")

                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setMessage("멤버를 강퇴하시겠습니까?")
                            .setPositiveButton("예") { dialog, _ ->
                                socketRequestManager.sendKickOutUser(partyNo, ownerMemNo, kickoutMemNo)
                                dialog.dismiss()
                            }
                            .setNegativeButton("취소"){ dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
            }


            Glide.with(itemView.context)
                .load(detailMember.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgDetailPartyProfile)

            binding.txtDetailPartyName.text = detailMember.nickName


            if (partyData?.memNo == currentUserMemNo && detailMember.memNo != currentUserMemNo) {
                binding.btnDetailPartyList.visibility = View.VISIBLE
            } else {
                binding.btnDetailPartyList.visibility = View.GONE
            }

        }
    }
}
