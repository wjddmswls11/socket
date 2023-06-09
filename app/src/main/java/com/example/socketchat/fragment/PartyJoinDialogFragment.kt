package com.example.socketchat.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.R
import com.example.socketchat.adapter.PartyJoinAdapter
import com.example.socketchat.data.NtRequestJoinPartyResponse
import com.example.socketchat.databinding.FragmentPartyJoinDialogBinding
import com.example.socketchat.viewmodel.ChatViewModel
class PartyJoinDialogFragment : DialogFragment() {

    private lateinit var binding : FragmentPartyJoinDialogBinding
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var partyJoinAdapter : PartyJoinAdapter
    private var partyJoinList: List<NtRequestJoinPartyResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPartyJoinDialogBinding.inflate(inflater, container, false)
        chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]

        // 전달받은 회원인증 번호
        val currentUserMemNo = arguments?.getInt("currentUserMemNo", -1) ?: -1

        //파티입장과 취소를 눌럿을 때
        partyJoinAdapter = PartyJoinAdapter(currentUserMemNo).apply {
            onAcceptClick = {
                dismiss()
            }
            onCancleClick = {
                dismiss()
            }
        }

        binding.rclPartyJoinResponse.adapter = partyJoinAdapter
        binding.rclPartyJoinResponse.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)




        return binding.root
    }

    //다이얼로그 프래그먼트에서 전달된 partyJoinList를 받아와서 어댑터에 데이터를 설정하는 역할
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serializable = arguments?.getSerializable("partyJoinList")
        Log.d("PartyJoinDialogFragment", "partyJoinList: $serializable")
        val partyJoinList = serializable as? List<NtRequestJoinPartyResponse>
        if (partyJoinList != null) {
            val arrayList = ArrayList(partyJoinList)
            Log.d("2PartyJoinDialogFragment", "$partyJoinList")
            partyJoinAdapter.setData(arrayList)
        }
    }


    //다이어로그의 형태를 결정
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.PartyJoinDialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

}