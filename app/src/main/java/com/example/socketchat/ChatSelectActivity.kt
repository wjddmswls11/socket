package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.adapter.ChatListAdapter
import com.example.socketchat.data.SummaryUserInfoResponse
import com.example.socketchat.databinding.ActivityChatSelectBinding
import com.example.socketchat.request.SocketRequestManager
import com.example.socketchat.viewmodel.ChatViewModel
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch

class ChatSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatSelectBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var summaryViewModel: SummaryViewModel
    private lateinit var socketRequestManager: SocketRequestManager

    private lateinit var chatListAdapter: ChatListAdapter

    private lateinit var summaryData: SummaryUserInfoResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listMessage.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        summaryData = intent.getSerializableExtra("summaryData") as SummaryUserInfoResponse


        val currentUserMemNo = intent.getIntExtra("currentUserMemNo", 0)
        Log.d("ChatSelectActivity", "$summaryData")

        chatListAdapter = ChatListAdapter(currentUserMemNo, summaryData)  // chatListAdapter 초기화

        // chatListAdapter 설정
        binding.listMessage.adapter = chatListAdapter

        // ViewModelProvider를 통해 SummaryViewModel 인스턴스 생성
        summaryViewModel = ViewModelProvider(this)[SummaryViewModel::class.java]

        // ViewModelProvider를 통해 OneOnOneChatViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]


        // socketRequestManager 초기화
        socketRequestManager = SocketRequestManager()

        //ChatViewModel의 Flow값을 관찰하고 chatListAdapter에 전달하여 UI를 업데이트합니다.
        lifecycleScope.launch {
            viewModel.oneOnOneFlow.collect { oneOnOneChatData ->
                chatListAdapter.updateChatData(oneOnOneChatData)
                binding.listMessage.scrollToPosition(chatListAdapter.itemCount - 1)
            }
        }


        val fromMemNo = currentUserMemNo
        val toMemNo = summaryData.data.memNo

        //채팅 로그 요청
        summaryViewModel.fetchOneOnOneChat(fromMemNo, toMemNo)

        // 채팅 로그 요청
        lifecycleScope.launch {
            summaryViewModel.oneOnOneChatLogFlow.collect { chatDataList ->
                // 채팅 로그 요청이 완료된 후에 chatDataList를 설정하고 어댑터에 전달
                Log.d("ChatSelectActivity", "onCreate: 통신결과 $chatDataList")
                for (i in chatDataList.reversed()) {
                    Log.d("ChatSelectActivity", "for문 안: 통신결과 ${i.data}")
                    for (chat in i.data) {
                        chatListAdapter.addChatDataAtFront(chat)
                    }
                }
                (binding.listMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                    chatListAdapter.itemCount - 1
                )
            }
        }

        //position으로 가져온 값들을 넣어줌
        binding.txtChatNickName.text = summaryData.data.nickName

        Glide.with(this)
            .load(summaryData.data.mainProfileUrl)
            .transform(CircleCrop())
            .into(binding.imgSummaryProfile)


        //editText에 넣은 값, 회원 인증에서 가져온 값, position으로 가져온 값을 RequestManager에 보내줌
        binding.btnChatMessage.setOnClickListener {
            val msg = binding.editChatMessage.text.toString()
            if (msg.isNotBlank()) {
                val currentMemNo = intent.getIntExtra("currentUserMemNo", 0)
                val targetMemNo = summaryData.data.memNo
                socketRequestManager.send1On1ChatRequest(
                    msg,
                    currentMemNo,
                    targetMemNo
                )
                //보내고 난 후 editChatMessage를 비어있게
                binding.editChatMessage.text.clear()
            }
        }

    }
}