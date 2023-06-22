package com.example.socketchat.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.adapter.PartyListAdapter
import com.example.socketchat.data.ReJoinPartyResponse
import com.example.socketchat.data.ReJoinPartyResponseData
import com.example.socketchat.databinding.FragmentPartyListBinding
import com.example.socketchat.socket.SocketDataRepository
import com.example.socketchat.viewmodel.MenuApiViewModel
import com.example.socketchat.viewmodel.MenuViewModel
import kotlinx.coroutines.launch


class PartyListFragment : Fragment() {
    private lateinit var binding: FragmentPartyListBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val menuApiViewModel: MenuApiViewModel by activityViewModels()
    private lateinit var partyListAdapter: PartyListAdapter

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
            menuApiViewModel.fetchSummaryPartyList()
        }


        menuApiViewModel.fetchSummaryPartyList()

        partyListAdapter =
            PartyListAdapter(
                requireActivity(),
                arguments?.getInt("currentUserMemNo", -1) ?: -1,
                menuApiViewModel
            )

        //방정보 요청
        viewLifecycleOwner.lifecycleScope.launch {
            menuApiViewModel.summaryListSharedFlow.collect { partyList ->
                partyListAdapter.setData(ArrayList(partyList))
                partyListAdapter.notifyDataSetChanged()
                Log.d("PartyListFragment", "summaryListSharedFlow changed: $partyList")
            }
        }


        //파티입장
        viewLifecycleOwner.lifecycleScope.launch {
            SocketDataRepository.joinPartyFlow.collect { joinPartyResponse ->
                partyListAdapter.updateJoinPartyResponses(joinPartyResponse)

            }
        }





        binding.rclPartyList.adapter = partyListAdapter
        binding.rclPartyList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //파티입장의 값이 변경되었을 때(내가 요청을 보냈을 때)
        viewLifecycleOwner.lifecycleScope.launch {
            SocketDataRepository.joinPartyFlow.collect { flowValue ->
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
            SocketDataRepository.kickOutFlow.collect { kickOutFlow ->
                if (kickOutFlow.data.kickoutResult == 0) {
                    val partyNo = kickOutFlow.data.commonRePartyChatInfo.partyNo
                    showKickOutDialog(partyNo)
                }
                menuApiViewModel.fetchSummaryPartyList()
            }
        }

        //파티가입을 승인했을 때 새로고침이 될 수 있도록
        viewLifecycleOwner.lifecycleScope.launch {
            menuViewModel.ntUserJoinedPartyFlow.collect { ntUserJoinedPartyFlow ->
                val lastNtUserJoinedPartyFlow = ntUserJoinedPartyFlow.lastOrNull()
                if (lastNtUserJoinedPartyFlow?.errInfo?.errNo == 0) {
                    Log.d("파티 새로고침1", "$ntUserJoinedPartyFlow")
                    menuApiViewModel.fetchSummaryPartyList()
                }
            }
        }


        //파티가 사라졌을 때 토스트 메세지가 나올 수 있도록
        viewLifecycleOwner.lifecycleScope.launch {
            SocketDataRepository.destroyPartyFlow.collect { destroyPartyFlow ->
                val partyNo = destroyPartyFlow.data.partyNo
                val message = "파티 $partyNo 가 삭제되었습니다."
                Log.d("파티 삭제", message)
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                menuApiViewModel.fetchSummaryPartyList()
            }
        }


        //내가 파티를 떠났을 떄
        viewLifecycleOwner.lifecycleScope.launch {
            SocketDataRepository.leavePartyFlow.collect {
                menuApiViewModel.fetchSummaryPartyList()
            }
        }

        //누군가 파티를 떠났을 때
        viewLifecycleOwner.lifecycleScope.launch {
            SocketDataRepository.ntUserLeaveFlow.collect { ntLeavePartyFlow ->
                val partyNo = ntLeavePartyFlow.data.partyNo
                val memNo = ntLeavePartyFlow.data.memNo
                val message = "$partyNo 번 방에서 $memNo 가 나갔습니다."
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                menuApiViewModel.fetchSummaryPartyList()
            }
        }
    }


    //파티가입을 신청했을 때 denyReason에 따라서 보이는 글이 다르게
    private fun showDenyReasonDialog(reJoinPartyResponseData: ReJoinPartyResponseData) {
        val message = when (reJoinPartyResponseData.commonRe1On1ChatInfo.replyMsgNo) {
            0 -> "신청되었습니다"
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

    //파티에서 강퇴를 당했을 떄
    private fun showKickOutDialog(partyNo: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
            .setMessage("당신은 $partyNo 번 방에서 강퇴되었습니다.")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
        val kickOutDialog = dialogBuilder.create()
        kickOutDialog.show()
    }
}