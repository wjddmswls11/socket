package com.example.socketchat.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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
    private val summaryViewModel: SummaryViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()
    private val socketRequestManager: SocketRequestManager by lazy { SocketRequestManager() }
    private lateinit var partyListAdapter: PartyListAdapter

    private var ntJoinPartyFlow: NtRequestJoinPartyResponse? = null
    private var joinPartyFlow: ReJoinPartyResponse? = null

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


        summaryViewModel.fetchSummaryPartyList()

        partyListAdapter =
            PartyListAdapter(requireActivity(), arguments?.getInt("currentUserMemNo", -1) ?: -1, summaryViewModel)

        //방정보 요청
        viewLifecycleOwner.lifecycleScope.launch {
            summaryViewModel.summaryListSharedFlow.collect { partyList ->
                partyListAdapter.setData(ArrayList(partyList))
                partyListAdapter.notifyDataSetChanged()
                Log.d("PartyListFragment", "summaryListSharedFlow changed: $partyList")
            }
        }




        //파티입장
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.joinPartyFlow.collect { joinPartyResponse ->
                partyListAdapter.updateJoinPartyResponses(joinPartyResponse)

            }
        }

        binding.rclPartyList.adapter = partyListAdapter
        binding.rclPartyList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //파티입장의 값이 변경되었을 때(나에게 요청이 왔을 때)
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.ntJoinPartyFlow.collect { ntJoinFlowValue ->
                ntJoinPartyFlow = ntJoinFlowValue
                // joinPartyFlow의 값이 변경되었을 때 수행할 작업을 여기에 작성합니다.
                Log.d("1 PartyListFragment", "joinPartyFlow: $ntJoinPartyFlow")

                //방에 있는 멤버의 정보를 요청함
                val partyNo = ntJoinPartyFlow?.data?.summaryPartyInfo?.partyNo
                val ownerMemNo = ntJoinPartyFlow?.data?.summaryPartyInfo?.memNo
                Log.d("2 PartyListFragment", "partyNo: $partyNo, memNo: $ownerMemNo")
                if (partyNo != null && ownerMemNo != null) {
                    summaryViewModel.fetchPartyMember(partyNo, ownerMemNo)
                }
                Log.d("3 PartyListFragment", "ntJoinPartyFlow : $ntJoinPartyFlow")

                val rqMemNo = ntJoinFlowValue.data.rqUserInfo.memNo
                val nickName = ntJoinFlowValue.data.rqUserInfo.nickName

                if (partyNo != null && ownerMemNo != null) {
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
                val tempFlow = joinPartyFlow
                if (tempFlow != null) {
                    showDenyReasonDialog(tempFlow.data)
                }
            }
        }

        //파티에서 강퇴가 되었을 때
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.kickOutFlow.collect { kickOutFlow ->
                if (kickOutFlow.data.kickoutResult == 0){
                    KickoutDialog.showKickOutDialog(requireContext(), kickOutFlow.data.partyNo, requireActivity())
                }
            }
        }

        //파티가입을 승인했을 때 새로고침이 될 수 있도록
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.ntUserJoinedPartyFlow.collect {ntUserJoinedPartyFlow ->
                if (ntUserJoinedPartyFlow.errInfo.errNo == 0) {
                    summaryViewModel.fetchSummaryPartyList()
                }
            }
        }


    }





    //파티가입을 받았을 때
    private fun showAcceptPartyDialog(
        partyNo: Int,
        ownerMemNo: Int,
        rqMemNo: Int,
        nickName: String
    ) {
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
        val message = when (reJoinPartyResponseData.commonRe1On1ChatInfo.replyMsgNo) {
            0 -> "참여되었습니다"
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
}