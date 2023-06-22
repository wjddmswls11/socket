package com.example.socketchat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.MenuActivity
import com.example.socketchat.adapter.SummaryAdapter
import com.example.socketchat.databinding.FragmentFriendBinding
import com.example.socketchat.socket.SocketManager
import com.example.socketchat.viewmodel.MenuApiViewModel
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {

    private lateinit var binding: FragmentFriendBinding
    private val menuApiViewModel: MenuApiViewModel by activityViewModels()
    private val socketManager = SocketManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater, container, false)

        binding.btnFriendRight.setOnClickListener {
            socketManager.disconnect()
            val activity = requireActivity() as MenuActivity
            activity.supportFragmentManager.beginTransaction().remove(this).commit()
            activity.finish()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserMemNo = arguments?.getInt("currentUserMemNo")
        val currentUserNickName = arguments?.getString("currentUserNickName")
        val mainProfileUrl = arguments?.getString("mainProfileUrl")

        menuApiViewModel.fetchSummaryUserInfo(currentUserMemNo)

        setRecyclerView(currentUserMemNo, currentUserNickName, mainProfileUrl)


    }



        private fun setRecyclerView(
            currentUserMemNo: Int?,
            currentUserNickName: String?,
            mainProfileUrl: String?
        ) {
            val summaryAdapter = SummaryAdapter(requireActivity() as MenuActivity)
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
                menuApiViewModel.summarySharedFlow.collect { responseList ->
                    summaryAdapter.setData(responseList)
                }
            }
        }

    }


