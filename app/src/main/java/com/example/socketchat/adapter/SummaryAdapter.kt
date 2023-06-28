package com.example.socketchat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.OneOnOneChatActivity
import com.example.socketchat.MenuActivity
import com.example.socketchat.data.SummaryUserInfo
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ItemSummaryFriendBinding

class SummaryAdapter(private val activity : MenuActivity, private val currentUserInfo: SummaryUserInfo?): RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    private var summaryList : ArrayList<SummaryUserInfoResponse> = arrayListOf()

    fun setData(newData: ArrayList<SummaryUserInfoResponse>) {
        summaryList.clear()
        summaryList.addAll(newData)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val binding = ItemSummaryFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return summaryList.size
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val summary = summaryList[position]
        holder.bind(summary)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OneOnOneChatActivity::class.java)
            intent.putExtra("summaryData", summaryList[position])
            intent.putExtra("summaryUserInfo", currentUserInfo ?: return@setOnClickListener)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }


    inner class SummaryViewHolder(private val binding: ItemSummaryFriendBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(summary : SummaryUserInfoResponse) {
            binding.summary = summary
            binding.executePendingBindings()
        }
    }

}