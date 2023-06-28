package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.adapter.OneOnOneChatAdapter
import com.example.socketchat.data.SummaryUserInfo
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ActivityChatSelectBinding
import com.example.socketchat.viewmodel.OneOnOneViewModel
import com.example.socketchat.viewmodel.OneOnOneApiViewModel
import kotlinx.coroutines.launch

class OneOnOneChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatSelectBinding
    private val viewModel: OneOnOneViewModel by viewModels()
    private val oneOnOneApiViewModel: OneOnOneApiViewModel by viewModels()
    private val oneOnOneViewModel : OneOnOneViewModel by viewModels()

    private lateinit var chatListAdapter: OneOnOneChatAdapter

    private var lastMsgNo: Long = System.currentTimeMillis()

    //첫 실행인지 아닌지 판단하는 변수
    private var isFirstLaunch: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listMessage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val summaryData = intent.getSerializableExtra("summaryData") as SummaryUserInfoResponse
        val summaryUserInfo = intent.getParcelableExtra<SummaryUserInfo>("summaryUserInfo")



        viewModel.setupLobby()

        val currentUserMemNo = summaryUserInfo?.memNo ?: 0
        Log.d("ChatSelectActivity", "$summaryData")

        chatListAdapter =
            OneOnOneChatAdapter(currentUserMemNo, oneOnOneViewModel, this@OneOnOneChatActivity , summaryData)  // chatListAdapter 초기화

        // chatListAdapter 설정
        binding.listMessage.adapter = chatListAdapter

        //ChatViewModel의 1:1채팅 값을 관찰하고 chatListAdapter에 전달하여 UI를 업데이트합니다.
        lifecycleScope.launch {
            viewModel.oneOnOneFlow.collect { oneOnOneChatData ->
                chatListAdapter.updateChatData(oneOnOneChatData)
                binding.listMessage.scrollToPosition(chatListAdapter.itemCount - 1)
            }
        }

        //editText에 넣은 값, 회원 인증에서 가져온 값, position으로 가져온 값을 RequestManager에 보내줌
        binding.btnChatMessage.setOnClickListener {
            val msg = binding.editChatMessage.text.toString()
            if (msg.isNotBlank()) {
                val currentMemNo = summaryUserInfo?.memNo ?: 0
                val targetMemNo = summaryData.data.memNo
                viewModel.send1On1ChatRequest(
                    msg,
                    currentMemNo,
                    targetMemNo
                )
                //보내고 난 후 editChatMessage를 비어있게
                binding.editChatMessage.text.clear()
            }
        }


        //chatviewModel에 1:1채팅의 삭제 값을 관찰하고 chatListAdapter에 전달하여 UI를 업데이트합니다.
        lifecycleScope.launch {
            viewModel.deleteOneOnOneFlow.collect { deleteOneOnOneResponseData ->
                if (deleteOneOnOneResponseData.errInfo.errNo == 0) {
                    val deletedMsgNo = deleteOneOnOneResponseData.data.rqDelete1On1Chat.delMsgNo
                    chatListAdapter.deleteChatData(deletedMsgNo)
                }

                if (deleteOneOnOneResponseData.data.result == 2) {
                    Toast.makeText(
                        this@OneOnOneChatActivity,
                        "5분 이상 지난 메시지는 삭제할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        val toMemNo = summaryData.data.memNo

        //채팅 로그 요청
        oneOnOneApiViewModel.fetchOneOnOneChat(currentUserMemNo, toMemNo, lastMsgNo, false)
        Log.d("라스트 메시지", "lastMagNo $lastMsgNo")


        // 채팅 로그 요청
        lifecycleScope.launch {
            Log.d("로그 요청","1111")
            oneOnOneApiViewModel.oneOnOneChatLogFlow.collect { chatDataList ->
                Log.d("로그 요청","222222")
                var lastMsgNoPrev: Long
                var isEndOfData = false
                // 채팅 로그 요청이 완료된 후에 chatDataList를 설정하고 어댑터에 전달
                chatDataList.forEach { i ->
                    Log.d("로그 요청","333333")
                    if (i.data.isNotEmpty()) {
                        Log.d("로그 요청","4444444")
                        val lastMsgInData = i.data[i.data.size - 1]
                        Log.d("ChatSelectActivity", "i.data size: ${i.data.size}")

                        lastMsgNoPrev = lastMsgNo
                        lastMsgNo = lastMsgInData.data.commonRe1On1ChatInfo.msgNo

                        if (lastMsgNo == lastMsgNoPrev) {
                            isEndOfData = true
                        }
                        Log.d(
                            "3 ChatSelectActivity",
                            "lastMsgInData : $lastMsgInData msgNo : $lastMsgNo"
                        )
                    }

                    if (!isEndOfData) {
                        Log.d("로그 요청","555555555")
                        val chatList = i.data.filterNot { it.cmd == "ReJoinPartyResult"}
                        val reversedChatList = chatList.reversed()
                        chatListAdapter.addChatDataAtFront(reversedChatList)
                    }
                }
                if (isFirstLaunch) {
                    (binding.listMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                        chatListAdapter.itemCount - 1
                    )
                    isFirstLaunch = false
                }
            }
        }


        //채팅 로그 요청
        binding.listMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    oneOnOneApiViewModel.fetchOneOnOneChat(currentUserMemNo, toMemNo, lastMsgNo, false)
                }
            }
        })

        //position으로 가져온 값들을 넣어줌
        binding.txtChatNickName.text = summaryData.data.nickName

        Glide.with(this)
            .load(summaryData.data.mainProfileUrl)
            .transform(CircleCrop())
            .into(binding.imgSummaryProfile)


    }
}