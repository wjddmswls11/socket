package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.adapter.PartyChatAdapter
import com.example.socketchat.data.KickoutUserResponse
import com.example.socketchat.data.Party
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.ActivityPartyChatBinding
import com.example.socketchat.`object`.KickoutDialog
import com.example.socketchat.request.SocketRequestManager
import com.example.socketchat.viewmodel.ChatViewModel
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch


class PartyChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var socketRequestManager: SocketRequestManager
    private lateinit var summaryViewModel: SummaryViewModel

    private lateinit var partyChatAdapter: PartyChatAdapter

    private lateinit var kickOutFlowValue: KickoutUserResponse

    private var lastMasNo : Long = System.currentTimeMillis()


    //스크롤이 가장 위로 올라갔을 때 이 변수가 false인 경우에만 새로운 채팅 로그 요청
    private var isFetchingChatLog: Boolean = false

    //첫 실행인지 아닌지 판단하는 변수
    private var isFirstLaunch : Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityPartyChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.partyListMessage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        //ViewModelProvider를 통해 ChatViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // ViewModelProvider를 통해 SummaryViewModel 인스턴스 생성
        summaryViewModel = ViewModelProvider(this).get(SummaryViewModel::class.java)

        // socketRequestManager 초기화
        socketRequestManager = SocketRequestManager()

        val currentUserMemNo = intent.getIntExtra("currentUserMemNo", -1)
        val partyData = intent.getSerializableExtra("partyData") as Party
        val partyMemberList =
            intent.getParcelableArrayListExtra<RePartyMemberListResponse>("partyMemberList")

        val kotlinPartyMemberList = ArrayList(partyMemberList)

        // PartyChatActivity에서 kickOutFlowValue를 받기 위해 getIntent() 메서드로 Intent를 가져온 후 getParcelableExtra를 사용하여 값을 가져옵니다.
        val kickOutFlowValue = intent.getParcelableExtra<KickoutUserResponse>("kickOutFlowValue")

        kickOutFlowValue?.let {
            val partyNo = it.data.partyNo
            KickoutDialog.showKickOutDialog(this, partyNo, this)
        }


        Log.d("PartyChatActivity", "currentUserMemNo: $currentUserMemNo")
        Log.d("PartyChatActivity", "partyData: $partyData")
        Log.d("PartyChatActivity", "partyMemberList: $partyMemberList")


        partyChatAdapter = PartyChatAdapter(currentUserMemNo, partyData, kotlinPartyMemberList)

        //partyChatAdapter 설정
        binding.partyListMessage.adapter = partyChatAdapter


        lifecycleScope.launch {
            viewModel.partyChatFlow.collect { partyChatResponse ->
                for (chatData in partyChatResponse){
                    partyChatAdapter.updatePartyChatDataList(chatData)
                }
                binding.partyListMessage.scrollToPosition(partyChatAdapter.itemCount -1)
            }
        }


        val partyNo = partyData.partyNo
        val rqMemNo = currentUserMemNo
        val lastMsgNo = System.currentTimeMillis()
        Log.d("PartyChatActivity", "partyNo: $partyNo, rqMemNo : $rqMemNo , lastMsgNo: $lastMsgNo")

        //파티 채팅 로그 요청
        summaryViewModel.fetchPartyChatLog(partyNo, rqMemNo, lastMsgNo)

        //파티 채팅 로그 요청
        lifecycleScope.launch {
            summaryViewModel.partyChatLogFlow.collect { partyChatList ->
                val oldSize = partyChatAdapter.itemCount
                Log.d("PartyChatActivity", "onCreate: 통신결과 $partyChatList")


                for (i in partyChatList.reversed()){

                    if (i.data?.isNotEmpty() == true){
                        val lastMsgInData = i.data[i.data.size - 1]
                        lastMasNo = lastMsgInData.data.commonRePartyChatInfo?.msgNo ?: lastMasNo
                        Log.d("PartyChatActivity", "lastMsgInData : $lastMsgInData msgNo : $lastMasNo")
                    }

                    Log.d("PartyChatActivity", "onCreate: for문 안:  $partyChatList")
                    for (chat in i.data){
                        partyChatAdapter.addPartyChatDataAtFront(chat)
                    }

                    //이전의 채팅 로그를 로그했을 때
                    if (!isFirstLaunch) {
                        // 이전에 보이던 첫 번째 메시지가 여전히 보이도록 스크롤 위치를 조정
                        (binding.partyListMessage.layoutManager as LinearLayoutManager).scrollToPosition(partyChatAdapter.itemCount - oldSize)
                    }else {
                        (binding.partyListMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                            partyChatAdapter.itemCount - 1
                        )
                        isFirstLaunch = false
                    }
                }
            }
        }

        //파티 채팅 로그 요청
        var isFirstLaunch = true
        binding.partyListMessage.setOnScrollChangeListener { v, _, _, _, _ ->
            val layoutManager = (v as? RecyclerView)?.layoutManager as? LinearLayoutManager
            if (!isFetchingChatLog && !isFirstLaunch && layoutManager?.findFirstCompletelyVisibleItemPosition() == 0) {
                isFetchingChatLog = true
                summaryViewModel.fetchPartyChatLog(partyNo, rqMemNo, lastMasNo)
            }else  {
                isFirstLaunch = false
            }
        }




        binding.btnPartyChatMessage.setOnClickListener {
            val msg = binding.editPartyChatMessage.text.toString()
            if (msg.isNotBlank()) {
                val fromMemNo = intent.getIntExtra("currentUserMemNo", -1)
                val partyNo = partyData.partyNo
                socketRequestManager.sendPartyChat(
                    msg,
                    fromMemNo,
                    partyNo
                )
                binding.editPartyChatMessage.text.clear()

            }

        }



        binding.imgPartyChatLogout.setOnClickListener {
            val partyNo = partyData.partyNo
            val memNo = currentUserMemNo

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("파티를 떠나시겠습니가?")
            dialogBuilder.setPositiveButton("네") { _, _ ->
                socketRequestManager.sendLeaveParty(partyNo, memNo)
                finish()
            }
            dialogBuilder.setNegativeButton("아니요") { _, _ ->
                finish()
            }
            val dialog = dialogBuilder.create()
            dialog.show()
        }


    }
}