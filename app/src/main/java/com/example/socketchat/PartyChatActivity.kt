package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.adapter.PartyChatAdapter
import com.example.socketchat.data.Party
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.ActivityPartyChatBinding
import com.example.socketchat.request.SocketRequestManager
import com.example.socketchat.viewmodel.ChatViewModel
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch


class PartyChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyChatBinding

    private val viewModel: ChatViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()

    private val socketRequestManager: SocketRequestManager by lazy { SocketRequestManager() }

    private lateinit var partyChatAdapter: PartyChatAdapter

    private var lastMasNo: Long = System.currentTimeMillis()


    //스크롤이 가장 위로 올라갔을 때 이 변수가 false인 경우에만 새로운 채팅 로그 요청
    private var isFetchingChatLog: Boolean = false

    //첫 실행인지 아닌지 판단하는 변수
    private var isFirstLaunch: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityPartyChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.partyListMessage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val currentUserMemNo = intent.getIntExtra("currentUserMemNo", -1)
        val partyData = intent.getSerializableExtra("partyData") as Party
        val partyMemberList =
            intent.getParcelableArrayListExtra<RePartyMemberListResponse>("partyMemberList") ?: arrayListOf()

        val kotlinPartyMemberList = ArrayList(partyMemberList)

        Log.d("PartyChatActivity", "currentUserMemNo: $currentUserMemNo")
        Log.d("PartyChatActivity", "partyData: $partyData")
        Log.d("PartyChatActivity", "partyMemberList: $partyMemberList")


        partyChatAdapter = PartyChatAdapter(currentUserMemNo, partyData, kotlinPartyMemberList)

        //partyChatAdapter 설정
        binding.partyListMessage.adapter = partyChatAdapter


        lifecycleScope.launch {
            viewModel.partyChatFlow.collect { partyChatResponse ->
                partyChatAdapter.updatePartyChatDataList(partyChatResponse)
                binding.partyListMessage.scrollToPosition(partyChatAdapter.itemCount - 1)
            }
        }


        val partyNo = partyData.partyNo
        val rqMemNo = currentUserMemNo
        val lastMsgNo = System.currentTimeMillis()
        Log.d("PartyChatActivity", "partyNo: $partyNo, rqMemNo : $rqMemNo , lastMsgNo: $lastMsgNo, mainPhotoUrl : ${partyData.mainPhotoUrl}")

        //파티 채팅 로그 요청
        summaryViewModel.fetchPartyChatLog(partyNo, rqMemNo, lastMsgNo, false)

        //파티 채팅 로그 요청
        lifecycleScope.launch {
            summaryViewModel.partyChatLogFlow.collect { partyChatList ->
                val oldSize = partyChatAdapter.itemCount
                Log.d("PartyChatActivity", "onCreate: 통신결과 $partyChatList")


                for (i in partyChatList) {
                    if (i.data.isNotEmpty()){
                        val lastMsgInData = i.data[i.data.size - 1]
                        lastMasNo = lastMsgInData.data.commonRePartyChatInfo.msgNo
                        Log.d(
                            "PartyChatActivity",
                            "lastMsgInData : $lastMsgInData msgNo : $lastMasNo"
                        )
                    }

                    Log.d("PartyChatActivity", "onCreate: for문 안:  $partyChatList")
                    for (chat in i.data) {
                        if (chat?.cmd == "RePartyTextChat" || chat?.cmd == "NtPartyTextChat") {
                            partyChatAdapter.addPartyChatDataAtFront(chat)
                        }
                    }
                }
                //이전의 채팅 로그를 로그했을 때
                if (!isFirstLaunch) {
                    // 이전에 보이던 첫 번째 메시지가 여전히 보이도록 스크롤 위치를 조정
                    (binding.partyListMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                        partyChatAdapter.itemCount - oldSize
                    )
                } else {
                    (binding.partyListMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                        partyChatAdapter.itemCount - 1
                    )
                    isFirstLaunch = false
                }
            }
        }

        //파티 채팅 로그 요청
        binding.partyListMessage.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isFetchingChatLog && layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    // 스크롤이 맨 위에 도착했을 때
                    isFetchingChatLog = true
                    summaryViewModel.fetchPartyChatLog(partyNo, rqMemNo, lastMasNo, false)
                }
            }
        })





        binding.btnPartyChatMessage.setOnClickListener {
            val msg = binding.editPartyChatMessage.text.toString()
            if (msg.isNotBlank()) {
                val fromMemNo = intent.getIntExtra("currentUserMemNo", -1)
                val partyNoEdit = partyData.partyNo
                socketRequestManager.sendPartyChat(
                    msg,
                    fromMemNo,
                    partyNoEdit
                )
                binding.editPartyChatMessage.text.clear()

            }

        }



        binding.imgPartyChatLogout.setOnClickListener {
            val dialogPartyNo = partyData.partyNo
            val memNo = currentUserMemNo

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("파티를 떠나시겠습니가?")
            dialogBuilder.setPositiveButton("네") { _, _ ->
                socketRequestManager.sendLeaveParty(dialogPartyNo, memNo)
                finish()
            }
            dialogBuilder.setNegativeButton("아니요") { _, _ ->
                finish()
            }
            val dialog = dialogBuilder.create()
            dialog.show()
        }



        //position으로 가져온 값들 넣어주기
        binding.txtPartyTitleName.text = partyData.title



    }





}