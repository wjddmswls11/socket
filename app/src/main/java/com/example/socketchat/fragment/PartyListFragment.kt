package com.example.socketchat.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.adapter.PartyListAdapter
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.data.Party
import com.example.socketchat.data.ReJoinPartyResponse
import com.example.socketchat.data.ReJoinPartyResponseData
import com.example.socketchat.databinding.FragmentPartyListBinding
import com.example.socketchat.`object`.KickoutDialog
import com.example.socketchat.request.SocketRequestManager
import com.example.socketchat.viewmodel.ChatViewModel
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch


class PartyListFragment : Fragment() {
    private lateinit var binding: FragmentPartyListBinding
    lateinit var summaryViewModel: SummaryViewModel
    private lateinit var socketRequestManager: SocketRequestManager
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var partyListAdapter: PartyListAdapter

    private var ntJoinPartyFlow: List<NtRequestJoinPartyResponse> = emptyList()
    private var joinPartyFlow: List<ReJoinPartyResponse> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPartyListBinding.inflate(inflater, container, false)


        //파티 생성 프래그먼트
        binding.btnCreateRoom.setOnClickListener {
            val dialogFragment = CreatePartyDialogFragment()
            val arguments = Bundle()
            arguments.putInt("currentUserMemNo", requireArguments().getInt("currentUserMemNo", -1))
            dialogFragment.arguments = arguments
            dialogFragment.show(childFragmentManager, "CreateCustomDialogFragment")
        }

        //새로고침
        binding.imgPartyChatListRight.setOnClickListener {
            summaryViewModel.fetchSummaryPartyList()
        }


        partyListAdapter =
            PartyListAdapter(requireActivity(), arguments?.getInt("currentUserMemNo", -1) ?: -1)

        summaryViewModel = ViewModelProvider(requireActivity())[SummaryViewModel::class.java]
        chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        socketRequestManager = SocketRequestManager()

        //방정보 요청
        viewLifecycleOwner.lifecycleScope.launch {
            summaryViewModel.summaryListSharedFlow.collect { partyList ->
                partyListAdapter.setData(ArrayList(partyList))
                partyListAdapter.notifyDataSetChanged()
                Log.d("PartyListFragment", "summaryListSharedFlow changed: $partyList")
            }
        }

        summaryViewModel.fetchSummaryPartyList()


        //파티입장
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.joinPartyFlow.collect { joinPartyResponse ->
                partyListAdapter.updateJoinPartyResponses(joinPartyResponse)

            }
        }

        binding.rclPartyList.adapter = partyListAdapter
        binding.rclPartyList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        //삭제 버튼을 눌렀을 때
        partyListAdapter.setOnDeleteClickListener { party ->
            showDeleteConfirmationDialog(party)
        }





        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //파티입장의 값이 변경되었을 때(나에게 요청이 왔을 때)
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.ntJoinPartyFlow.collect { ntJoinFlowValue ->
                ntJoinPartyFlow = ntJoinFlowValue
                // joinPartyFlow의 값이 변경되었을 때 수행할 작업을 여기에 작성합니다.
                Log.d("PartyListFragment", "joinPartyFlow: $ntJoinPartyFlow")
                if (ntJoinPartyFlow.isNotEmpty()) {

                    //방에 있는 멤버의 정보를 요청함
                    val partyNo = ntJoinPartyFlow[0].data.SummaryPartyInfo.partyNo
                    val ownerMemNo = ntJoinPartyFlow[0].data.SummaryPartyInfo.memNo
                    Log.d("PartyListFragment",  "partyNo: $partyNo, memNo: $ownerMemNo")
                    summaryViewModel.fetchPartyMember(partyNo, ownerMemNo)
                    Log.d("PartyListFragment",  "ntJoinPartyFlow : $ntJoinPartyFlow")

                    val rqMemNo = ntJoinFlowValue[0].data.RqUserInfo.memNo
                    val nickName = ntJoinFlowValue[0].data.RqUserInfo.nickName

                    showAcceptPartyDialog(partyNo, ownerMemNo, rqMemNo, nickName)
                }
            }
        }

        //파티입장의 값이 변경되었을 때(내가 요청을 보냈을 때)
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.joinPartyFlow.collect { flowValue ->
                joinPartyFlow = flowValue
                // joinPartyFlow의 값이 변경되었을 때 수행할 작업을 여기에 작성합니다.
                Log.d("PartyListFragment", "joinPartyFlow: $joinPartyFlow")
                if (joinPartyFlow.isNotEmpty()) {
                    showDenyReasonDialog(joinPartyFlow[0].data)
                }
            }
        }

        //파티에서 강퇴가 되었을 때
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.kickOutFlow.collect { kickOutFlow ->
                kickOutFlow.forEach { kickoutUserResponse ->
                    if (kickoutUserResponse.data.kickoutResult == 0) {
                        KickoutDialog.showKickOutDialog(requireContext(), kickoutUserResponse.data.partyNo, requireActivity())
                    }
                }
            }
        }
    }

    //파티가입을 받았을 때
    private fun showAcceptPartyDialog(partyNo: Int, ownerMemNo: Int, rqMemNo: Int, nickName : String) {
        val message = "$nickName 가 $partyNo 번방 참여를 신청합니다"

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(message)
            .setPositiveButton("수락") { _, _ ->
                // 파티 참여 승락 처리를 진행하세요.
                socketRequestManager.sendAcceptParty(partyNo, true, ownerMemNo, rqMemNo)
            }
            .setNegativeButton("거절") { _, _ ->
                // 파티 참여 거절 처리를 진행하세요.
                socketRequestManager.sendAcceptParty(partyNo, false, ownerMemNo, rqMemNo)
            }
            .create()
            .show()
    }



    //파티가입을 신청했을 때 denyReason에 따라서 보이는 글이 다르게
    private fun showDenyReasonDialog(reJoinPartyResponseData: ReJoinPartyResponseData) {
        val message = when (reJoinPartyResponseData.denyReason) {
            1 -> "방장이 거절했습니다."
            2 -> "빈방이 없습니다."
            3 -> "방이 꽉 찼습니다."
            4 -> "이미 참석해 있습니다."
            5 -> "이미 참석 신청을 해놓았습니다."
            6 -> "강퇴 유저입니다."
            7 -> "방장 승락대기중입니다."
            else -> "기타"
        }

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(message)
            .setPositiveButton("예") { _, _ ->
            }
            .create()
            .show()
    }


    //파티를 삭제할 수 있도록
    private fun showDeleteConfirmationDialog(party: Party) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("파티를 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                val partyNo = party.partyNo
                val ownerMemNo = party.memNo

                summaryViewModel.setDestroyPartyRequest(partyNo, ownerMemNo)
            }
            .setNegativeButton("아니오") { _, _ ->
                // 아무 작업도 수행하지 않음
            }
            .create()
            .show()


    }

}