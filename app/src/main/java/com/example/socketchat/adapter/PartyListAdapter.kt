package com.example.socketchat.adapter

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.R
import com.example.socketchat.data.Party
import com.example.socketchat.data.ReJoinPartyResponse
import com.example.socketchat.databinding.ItemPartyListBinding
import com.example.socketchat.fragment.DialogDetailPartyFragment
import com.example.socketchat.viewmodel.SummaryViewModel

class PartyListAdapter(private val fragmentActivity : FragmentActivity, private val currentUserMemNo: Int, private val summaryViewModel: SummaryViewModel) :
    RecyclerView.Adapter<PartyListAdapter.PartyViewHolder>() {

    private var partyList: ArrayList<Party> = arrayListOf()
    private var joinPartyResponses : List<ReJoinPartyResponse> = listOf()

    fun setData(newData: ArrayList<Party>) {
        partyList.clear()
        partyList.addAll(newData)
        notifyDataSetChanged()
    }

    fun updateJoinPartyResponses(newJoinPartyResponses: ReJoinPartyResponse) {
        joinPartyResponses = joinPartyResponses + newJoinPartyResponses
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        val binding =
            ItemPartyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PartyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return partyList.size
    }

    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        val party = partyList[position]
        holder.bind(party)
        holder.itemView.setOnClickListener {
            val dialogFragment = DialogDetailPartyFragment()
            val arguments = Bundle().apply {
                putSerializable("partyData", party)
                putInt("currentUserMemNo", currentUserMemNo)
            }
            dialogFragment.arguments = arguments
            val fragmentManager = fragmentActivity.supportFragmentManager
            dialogFragment.show(fragmentManager, "DialogDetailPartyFragment")
        }


        //삭제의 경우
        holder.binding.btnDestroyPartyList.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            dialogBuilder.setMessage("파티를 삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    val partyNo = party.partyNo
                    val ownerMemNo = party.memNo

                    summaryViewModel.fetchDestroyParty(partyNo, ownerMemNo)
                }
                .setNegativeButton("아니오") { _, _ ->
                    // 아무 작업도 수행하지 않음
                }
                .create()
                .show()

        }

    }

    inner class PartyViewHolder(val binding: ItemPartyListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(partyList: Party) {

            binding.txtPartyListTitle.text = partyList.title
            binding.txtPartyListMemNo.text = partyList.memNo.toString()
            binding.txtPartyListCurMemberCount.text = partyList.curMemberCount.toString()
            binding.txtPartyListMaxMemberCount.text = partyList.maxMemberCount.toString()

            if (partyList.memNo == currentUserMemNo) {
                binding.btnDestroyPartyList.visibility = View.VISIBLE
            } else {
                binding.btnDestroyPartyList.visibility = View.GONE
            }

           if (partyList.isAutoJoin) {
               binding.imgPartyListAutoJoin.setImageResource(R.drawable.baseline_lock_open_24)
           }else{
               binding.imgPartyListAutoJoin.setImageResource(R.drawable.baseline_lock_24)
           }
        }
    }

}