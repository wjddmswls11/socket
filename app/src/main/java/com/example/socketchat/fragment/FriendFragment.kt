package com.example.socketchat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.adapter.SummaryAdapter
import com.example.socketchat.databinding.FragmentFriendBinding
import com.example.socketchat.`object`.KickoutDialog
import com.example.socketchat.viewmodel.ChatViewModel
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {

    private lateinit var binding: FragmentFriendBinding
    private val summaryViewModel: SummaryViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserMemNo = arguments?.getInt("currentUserMemNo")
        val currentUserNickName = arguments?.getString("currentUserNickName")
        val mainProfileUrl = arguments?.getString("mainProfileUrl")

        summaryViewModel.fetchSummaryUserInfo(currentUserMemNo)

        setRecyclerView(currentUserMemNo, currentUserNickName, mainProfileUrl)


        //파티에서 강퇴가 되었을 때
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.kickOutFlow.collect { kickOutFlow ->
                if (kickOutFlow.data.kickoutResult == 0) {
                    val partyNo = kickOutFlow.data.partyNo
                    partyNo.let {
                        KickoutDialog.showKickOutDialog(requireContext(), it, requireActivity())
                    }
                }
            }
        }

    }
        private fun setRecyclerView(
            currentUserMemNo: Int?,
            currentUserNickName: String?,
            mainProfileUrl: String?
        ) {
            val summaryAdapter = SummaryAdapter()
            summaryAdapter.setCurrentUserMemNo(currentUserMemNo ?: 0)
            summaryAdapter.setCurrentUserNickName(currentUserNickName)
            summaryAdapter.setMainProfileUrl(mainProfileUrl)

            binding.summaryRcl001.apply {
                adapter = summaryAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }

            //사용자 정보 요청
            viewLifecycleOwner.lifecycleScope.launch {
                summaryViewModel.summarySharedFlow.collect { responseList ->
                    summaryAdapter.setData(responseList)
                }
            }
        }

    }


