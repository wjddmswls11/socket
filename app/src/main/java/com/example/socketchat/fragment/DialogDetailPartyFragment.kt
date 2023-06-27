package com.example.socketchat.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.MenuActivity
import com.example.socketchat.PartyChatActivity
import com.example.socketchat.R
import com.example.socketchat.adapter.DetailPartyAdapter
import com.example.socketchat.data.Party
import com.example.socketchat.data.RePartyMemberListResponse
import com.example.socketchat.databinding.FragmentDialogDetailPartyBinding
import com.example.socketchat.socket.SocketDataRepository
import com.example.socketchat.viewmodel.MenuApiViewModel
import com.example.socketchat.viewmodel.MenuViewModel
import kotlinx.coroutines.launch


class DialogDetailPartyFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogDetailPartyBinding
    private val menuApiViewModel: MenuApiViewModel by activityViewModels()
    private val menuViewModel : MenuViewModel by activityViewModels()
    private lateinit var detailPartyAdapter: DetailPartyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogDetailPartyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val partyData = arguments?.get("partyData") as Party?
        val currentUserMemNo = arguments?.getInt("currentUserMemNo", -1) ?: -1

        //방 상세정보 리퀘스트 정보 전달
        partyData?.partyNo?.let {
            menuApiViewModel.fetchDetailParty(it)
        } ?: run {
            Toast.makeText(requireActivity(), "방 리스트의 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }


        //방 멤버 요청 리퀘스트 정보 전달
        val partyNo = partyData.partyNo
        val ownerMemNo = partyData.memNo


        menuApiViewModel.fetchPartyMember(partyNo, ownerMemNo)



        detailPartyAdapter = DetailPartyAdapter(requireActivity() as MenuActivity, menuViewModel)
        detailPartyAdapter.setPartyData(partyData)
        detailPartyAdapter.setCurrentUserMemNo(currentUserMemNo)
        binding.rclDetailParty.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rclDetailParty.adapter = detailPartyAdapter


        //유저강퇴를 하고 새로고침을 해 주는 곳
        lifecycleScope.launch {
            SocketDataRepository.ntUserLeaveFlow.collect { ntUserLeaveFlow ->
                if (ntUserLeaveFlow.errInfo.errNo == 0) {
                    menuApiViewModel.fetchPartyMember(partyNo, ownerMemNo)
                    menuApiViewModel.fetchDetailParty(partyNo)
                    menuApiViewModel.fetchSummaryPartyList()
                }
            }
        }


        //방 멤버를 요청해서 자신이 들어가 있는지 구분
        lifecycleScope.launch {
            menuApiViewModel.partyMemberList.collect { partyMembers ->
                val memNos = partyMembers.flatMap { it.data.map { it.memNo } }
                Log.d("DialogDetailPartyFragment", "Party Member List: $memNos")

                val memNosList = ArrayList(memNos)
                Log.d("DialogDetailPartyFragment", "Member Numbers: $memNosList")

                val matchingMember = memNosList.find { it == currentUserMemNo }
                Log.d("DialogDetailPartyFragment", "matchingMember: $matchingMember")

                binding.dialogDetailPartyCancel.setOnClickListener {
                    dismiss()
                }

                //방에 들어갈 수 있게 하는 곳
                if (matchingMember != null) {
                    binding.dialogDetailJoinParty.visibility = View.GONE
                    binding.dialogDetailPartyPartyChat.visibility = View.VISIBLE

                    binding.dialogDetailPartyPartyChat.setOnClickListener {
                        val intent = Intent(requireContext(), PartyChatActivity::class.java)
                        intent.putExtra("currentUserMemNo", currentUserMemNo)
                        intent.putExtra("partyData", partyData)

                        val bundle = Bundle()
                        bundle.putParcelableArrayList(
                            "partyMemberList",
                            ArrayList<RePartyMemberListResponse>(partyMembers)
                        )
                        intent.putExtras(bundle)

                        startActivity(intent)
                        dismiss()
                    }
                } else {
                    binding.dialogDetailJoinParty.visibility = View.VISIBLE
                    binding.dialogDetailPartyPartyChat.visibility = View.GONE
                }

                detailPartyAdapter.updateMemberList(ArrayList(partyMembers.flatMap { it.data }))
                detailPartyAdapter.setData(ArrayList(partyMembers.flatMap { it.data }))
            }
        }


        //파티신청 버튼을 눌렀을 경우 리퀘스트를 보낼 수 있게 함
        lifecycleScope.launch {
            menuApiViewModel.detailPartyContext.collect { detailPartyResponses ->
                if (detailPartyResponses.isNotEmpty()) {
                    val response = detailPartyResponses[0]

                    binding.dialogDetailTitle.text = response.data.summaryPartyInfo.title
                    binding.dialogDetailMemNo.text = response.data.summaryPartyInfo.memNo.toString()
                    binding.dialogDetailCurMem.text =
                        response.data.summaryPartyInfo.curMemberCount.toString()
                    binding.dialogDetailMaxMem.text =
                        response.data.summaryPartyInfo.maxMemberCount.toString()

                    binding.dialogDetailJoinParty.setOnClickListener {
                        val partyNoDetail = response.data.summaryPartyInfo.partyNo
                        val ownerMemNoDetail = response.data.summaryPartyInfo.memNo
                        SocketDataRepository.sendJoinPartyRequest(partyNoDetail, ownerMemNoDetail, currentUserMemNo)

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