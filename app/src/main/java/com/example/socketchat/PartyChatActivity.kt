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
import com.example.socketchat.socket.SocketDataRepository
import com.example.socketchat.viewmodel.PartyChatViewModel
import com.example.socketchat.viewmodel.PartyChatAPiViewModel
import kotlinx.coroutines.launch


class PartyChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyChatBinding

    private val viewModel: PartyChatViewModel by viewModels()
    private val partyChatAPiViewModel: PartyChatAPiViewModel by viewModels()

    private lateinit var partyChatAdapter: PartyChatAdapter

    private var lastMasNo: Long = System.currentTimeMillis()

    //첫 실행인지 아닌지 판단하는 변수
    private var isFirstLaunch: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPartyChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.setupParty()

        binding.partyListMessage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val currentUserMemNo = intent.getIntExtra("currentUserMemNo", -1)
        val partyData = intent.getParcelableExtra<Party>("partyData")

        val partyMemberList =
            intent.getParcelableArrayListExtra<RePartyMemberListResponse>("partyMemberList")
                ?: arrayListOf()

        val kotlinPartyMemberList = ArrayList(partyMemberList)



        partyChatAdapter = PartyChatAdapter(kotlinPartyMemberList)

        //partyChatAdapter 설정
        binding.partyListMessage.adapter = partyChatAdapter


        lifecycleScope.launch {
            viewModel.partyChatFlow.collect { partyChatResponse ->
                partyChatAdapter.updatePartyChatDataList(partyChatResponse)
                binding.partyListMessage.scrollToPosition(partyChatAdapter.itemCount - 1)
            }
        }

        partyData?.let { nonNullPartyData ->
            val partyNo = nonNullPartyData.partyNo

            Log.d(
                "PartyChatActivity",
                "partyNo: $partyNo, lastMsgNo: $lastMasNo, mainPhotoUrl : ${partyData.mainPhotoUrl}"
            )

            //파티 채팅 로그 요청
            partyChatAPiViewModel.fetchPartyChatLog(partyNo, currentUserMemNo, lastMasNo, false)


            //파티 채팅 로그 요청
            binding.partyListMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) {
                        partyChatAPiViewModel.fetchPartyChatLog(
                            partyNo,
                            currentUserMemNo,
                            lastMasNo,
                            false
                        )
                    }
                }
            })


            //파티 채팅 로그 요청
            lifecycleScope.launch {
                partyChatAPiViewModel.partyChatLogFlow.collect { partyChatList ->
                    var lastMsgNoPrevParty: Long
                    var isEndOfData = false
                    Log.d("PartyChatActivity", "onCreate: 통신결과 $partyChatList")
                    partyChatList.forEach { i ->
                        if (i.data.isNotEmpty()) {
                            val lastMsgInData = i.data[i.data.size - 1]

                            lastMsgNoPrevParty = lastMasNo
                            lastMasNo = lastMsgInData.data.commonRePartyChatInfo.msgNo
                            if (lastMasNo == lastMsgNoPrevParty) {
                                isEndOfData = true
                            }

                            Log.d(
                                "PartyChatActivity",
                                "lastMsgInData : $lastMsgInData msgNo : $lastMasNo"
                            )
                        }

                        if (!isEndOfData) {
                            val reversedPartyChat = i.data.reversed()
                            partyChatAdapter.addPartyChatDataAtFront(reversedPartyChat)
                        }
                    }
                    //이전의 채팅 로그를 로그했을 때
                    if (isFirstLaunch) {
                        (binding.partyListMessage.layoutManager as LinearLayoutManager).scrollToPosition(
                            partyChatAdapter.itemCount - 1
                        )
                        isFirstLaunch = false
                    }
                }
            }


            //방삭제가 되면 finish가 될 수 있도록
            lifecycleScope.launch {
                SocketDataRepository.destroyPartyFlow.collect { destroyPartyFlow ->
                    if (destroyPartyFlow.errInfo.errNo == 0 && partyNo == destroyPartyFlow.data.partyNo) {
                        finish()
                    }
                }
            }

            //유저강퇴가 되면 finish가 될 수 있도록
            lifecycleScope.launch {
                SocketDataRepository.kickOutFlow.collect { kickOutFlow ->
                    if (kickOutFlow.errInfo.errNo == 0 && partyNo == kickOutFlow.data.commonRePartyChatInfo.partyNo && currentUserMemNo == kickOutFlow.data.kickoutMemNo) {
                        finish()
                    }
                }
            }


            binding.btnPartyChatMessage.setOnClickListener {
                val msg = binding.editPartyChatMessage.text.toString()
                if (msg.isNotBlank()) {
                    val fromMemNo = intent.getIntExtra("currentUserMemNo", -1)
                    val partyNoEdit = partyData.partyNo
                    viewModel.sendPartyChat(
                        msg,
                        fromMemNo,
                        partyNoEdit
                    )
                    binding.editPartyChatMessage.text.clear()

                }

            }


            binding.imgPartyChatLogout.setOnClickListener {
                val dialogPartyNo = partyData.partyNo

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("파티를 떠나시겠습니가?")
                dialogBuilder.setPositiveButton("네") { _, _ ->
                    SocketDataRepository.sendLeaveParty(dialogPartyNo, currentUserMemNo)
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


}