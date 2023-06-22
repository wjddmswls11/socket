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
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ItemSummaryFriendBinding

class SummaryAdapter(private val activity : MenuActivity): RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    private var currentUserMemNo: Int = 0
    private var currentUserNickName: String? = null
    private var mainProfileUrl: String? = null


    fun setCurrentUserMemNo(memNo: Int) {
        currentUserMemNo = memNo
    }

    fun setCurrentUserNickName(nickName: String?) {
        currentUserNickName = nickName
    }

    fun setMainProfileUrl(profileUrl: String?) {
        mainProfileUrl = profileUrl
    }


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
            intent.putExtra("currentUserMemNo", currentUserMemNo)
            intent.putExtra("currentUserNickName", currentUserNickName)
            intent.putExtra("mainProfileUrl", mainProfileUrl)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }




    inner class SummaryViewHolder(private val binding: ItemSummaryFriendBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(SummaryUserInfoResponse : SummaryUserInfoResponse) {
            Glide.with(activity)
                .load(SummaryUserInfoResponse.data.mainProfileUrl)
                .transform(CircleCrop())
                .into(binding.imgSummaryProfile)

            binding.txtSummaryName.text = SummaryUserInfoResponse.data.nickName

        }
    }

}