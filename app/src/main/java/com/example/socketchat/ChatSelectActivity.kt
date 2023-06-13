package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
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
    private val viewModel: ChatViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val socketRequestManager: SocketRequestManager by lazy { SocketRequestManager() }

    private lateinit var chatListAdapter: ChatListAdapter

    private lateinit var summaryData: SummaryUserInfoResponse

    private var lastMsgNo : Long = System.currentTimeMillis()

    //스크롤이 가장 위로 올라갔을 때 이 변수가 false인 경우에만 새로운 채팅 로그 요청
    private var isFetchingChatLog : Boolean = false

    //첫 실행인지 아닌지 판단하는 변수
    private var isFirstLaunch : Boolean = true

    //현재 스크롤 포지션
    private var currentScrollPosition : Int = 0


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

        val time = System.currentTimeMillis()
        Log.d("ChatSelectActivity 시간", "$time")

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





        val fromMemNo = currentUserMemNo
        val toMemNo = summaryData.data.memNo

        //chatViewModel의 1:1채팅 삭제 값을 관찰하고 chatListAdapter에 전달하여 UI를 업데이트합니다.
        lifecycleScope.launch {
            viewModel.deleteOneOnOneFlow.collect {
                currentScrollPosition = (binding.listMessage.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val last = System.currentTimeMillis()
                summaryViewModel.fetchOneOnOneChat(fromMemNo, toMemNo, last, false)
            }
        }



        //채팅 로그 요청
        summaryViewModel.fetchOneOnOneChat(fromMemNo, toMemNo, lastMsgNo,false)
        Log.d("라스트 메시지", "lastMagNo $lastMsgNo")
        // 채팅 로그 요청
        lifecycleScope.launch {
            summaryViewModel.oneOnOneChatLogFlow.collect { chatDataList ->
                val oldSize = chatListAdapter.itemCount
                // 채팅 로그 요청이 완료된 후에 chatDataList를 설정하고 어댑터에 전달
                Log.d("1 ChatSelectActivity", "onCreate: 통신결과 $chatDataList")
                for (i in chatDataList) {
                    Log.d("2 ChatSelectActivity", "for문 안: 통신결과 ${i.data}")
                    if (i.data.isNotEmpty()){
                        val lastMsgInData = i.data[i.data.size -1]
                        Log.d("ChatSelectActivity", "i.data size: ${i.data.size}")

                        lastMsgNo = lastMsgInData?.data?.commonRe1On1ChatInfo?.msgNo ?: lastMsgNo

                        Log.d(
                            "3 ChatSelectActivity",
                            "lastMsgInData : $lastMsgInData msgNo : $lastMsgNo"
                        )
                    }


                    for (chat in i.data) {
                        if ((chat?.cmd == "Re1On1TextChat" || chat?.cmd == "Nt1On1TextChat") && !chat.data.commonRe1On1ChatInfo.isDeleted) {
                            chatListAdapter.addChatDataAtFront(chat)
                        }
                    }
                }
                if (isFirstLaunch) {
                    (binding.listMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                        chatListAdapter.itemCount - 1
                    )
                    isFirstLaunch = false

//                }else if(currentScrollPosition > -1){
//                    (binding.listMessage.layoutManager as LinearLayoutManager).scrollToPosition(
//                        currentScrollPosition
//                    )
//                    currentScrollPosition = -1
                }else {
                    (binding.listMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                        chatListAdapter.itemCount - oldSize
                    )
                }
            }
        }


        //채팅 로그 요청
        binding.listMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isFetchingChatLog && layoutManager.findFirstVisibleItemPosition() == 0) {
                    //스크롤이 맨 위에 도착했을 때
                    isFetchingChatLog = true
                    summaryViewModel.fetchOneOnOneChat(fromMemNo, toMemNo, lastMsgNo, false)
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