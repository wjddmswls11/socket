package com.example.socketchat.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.PartyChatActivity
import com.example.socketchat.R
import com.example.socketchat.adapter.DetailPartyAdapter
import com.example.socketchat.data.KickoutUserResponse
import com.example.socketchat.data.Party
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.FragmentDialogDetailPartyBinding
import com.example.socketchat.request.SocketRequestManager
import com.example.socketchat.viewmodel.ChatViewModel
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class DialogDetailPartyFragment : DialogFragment() {

    private lateinit var binding : FragmentDialogDetailPartyBinding
    private lateinit var summaryViewModel : SummaryViewModel
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var detailPartyAdapter : DetailPartyAdapter
    private var kickOutFlowValue: KickoutUserResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogDetailPartyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        summaryViewModel = ViewModelProvider(requireActivity())[SummaryViewModel::class.java]
        chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]

        val partyData = arguments?.getSerializable("partyData") as? Party
        val currentUserMemNo = arguments?.getInt("currentUserMemNo", -1) ?: -1

        Log.d("DialogDetailPartyFragment", "PartyData: $partyData")
        Log.d("DialogDetailPartyFragment", "currentUserMemNo: $currentUserMemNo")

        //방 상세정보 리퀘스트 정보 전달
        summaryViewModel.setDetailPartyRequest(partyData?.partyNo ?: -1)


        //방 멤버 요청 리퀘스트 정보 전달
        val partyNo = partyData?.partyNo
        val ownerMemNo = partyData?.memNo
        summaryViewModel.fetchPartyMember(partyNo ?: -1, ownerMemNo ?: -1)

        Log.d("DialogDetailPartyFragment", "PartyData: $partyNo, currentUserMemNo : $ownerMemNo")


        detailPartyAdapter = DetailPartyAdapter(requireContext())
        detailPartyAdapter.setPartyData(partyData)
        detailPartyAdapter.setCurrentUserMemNo(currentUserMemNo)
        binding.rclDetailParty.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rclDetailParty.adapter = detailPartyAdapter


        lifecycleScope.launch {
            chatViewModel.kickOutFlow.collect { kickOutFlow ->
                kickOutFlowValue = if (kickOutFlow.isNotEmpty()){
                    kickOutFlow[0]
                } else {
                    null
                }
            }
        }



        lifecycleScope.launch {
            summaryViewModel.partyMemberList.collect { partyMembers ->
                Log.d("DialogDetailPartyFragment", "Collected partyMembers: $partyMembers")

                val memNos = partyMembers.flatMap { it.data.map { it.memNo } }
                Log.d("DialogDetailPartyFragment", "Party Member List: $memNos")

                val memNosList = ArrayList(memNos)
                Log.d("DialogDetailPartyFragment", "Member Numbers: $memNosList")

                val matchingMember = memNosList.find { it == currentUserMemNo }
                Log.d("DialogDetailPartyFragment", "matchingMember: $matchingMember")

                val memberInfolist = partyMembers.flatMap { it.data }
                Log.d("DialogDetailPartyFragment", "memberInfolist: $memberInfolist")

                binding.dialogDetailPartyCancel.setOnClickListener {
                    dismiss()
                }



                //일단 isAutoJoin으로 공개방인지 아닌지를 구분하여 들어갈수 있게 하고
                //비공개방일 경우 matchingMember가 일치하면 파티 입장이 나오도록 하고
                //일치하는 것이 없을 경우 파티신청을 하게 된다
                if (partyData?.isAutoJoin == true) {
                    binding.dialogDetailJoinParty.visibility = View.GONE
                    binding.dialogDetailPartyPartyChat.visibility = View.VISIBLE

                    binding.dialogDetailPartyPartyChat.setOnClickListener {
                        val intent = Intent(requireContext(), PartyChatActivity::class.java)
                        intent.putExtra("currentUserMemNo", currentUserMemNo)
                        intent.putExtra("partyData", partyData)
                        val bundle = Bundle()
                        bundle.putParcelable("kickOutFlowValue", kickOutFlowValue)
                        bundle.putParcelableArrayList("partyMemberList", ArrayList<RePartyMemberListResponse>(partyMembers))
                        intent.putExtras(bundle)


                        startActivity(intent)
                        dismiss()
                    }
                }else if (partyData?.isAutoJoin == false){
                    if (matchingMember != null){
                        binding.dialogDetailJoinParty.visibility = View.GONE
                        binding.dialogDetailPartyPartyChat.visibility = View.VISIBLE

                        binding.dialogDetailPartyPartyChat.setOnClickListener {
                            val intent = Intent(requireContext(), PartyChatActivity::class.java)
                            intent.putExtra("currentUserMemNo", currentUserMemNo)
                            intent.putExtra("partyData", partyData)
                            val bundle = Bundle()
                            bundle.putParcelable("kickOutFlowValue", kickOutFlowValue)
                            bundle.putParcelableArrayList("partyMemberList", ArrayList<RePartyMemberListResponse>(partyMembers))
                            intent.putExtras(bundle)

                            startActivity(intent)
                            dismiss()
                        }
                    }else {
                        binding.dialogDetailJoinParty.visibility = View.VISIBLE
                        binding.dialogDetailPartyPartyChat.visibility = View.GONE
                    }
                }
                detailPartyAdapter.updateMemberList(ArrayList(partyMembers.flatMap { it.data }))
                detailPartyAdapter.setData(ArrayList(partyMembers.flatMap { it.data }))
            }
        }


        //파티신청 버튼을 눌렀을 경우 리퀘스트를 보낼 수 있게 함
        lifecycleScope.launch {
            summaryViewModel.detailPartyContext.collect { detailPartyResponses ->
                if (detailPartyResponses.isNotEmpty()) {
                    val response = detailPartyResponses[0] // 첫 번째 response를 사용

                    binding.dialogDetailTitle.text = response.data.summaryPartyInfo.title
                    binding.dialogDetailPartyNo.text = response.data.summaryPartyInfo.partyNo.toString()
                    binding.dialogDetailMemNo.text = response.data.summaryPartyInfo.memNo.toString()
                    binding.dialogDetailCurMem.text = response.data.summaryPartyInfo.curMemberCount.toString()
                    binding.dialogDetailMaxMem.text = response.data.summaryPartyInfo.maxMemberCount.toString()

                    binding.dialogDetailJoinParty.setOnClickListener {
                        val partyNo = response.data.summaryPartyInfo.partyNo
                        val ownerMemNo = response.data.summaryPartyInfo.memNo
                        val rqMemNo = currentUserMemNo ?: -1
                        SocketRequestManager().senJoinPartyRequest(partyNo, ownerMemNo, rqMemNo)

                        dismiss()
                    }


                }
            }
        }



    }

    //다이얼로그의 형태를 결정하는 곳
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.CustomDialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

}