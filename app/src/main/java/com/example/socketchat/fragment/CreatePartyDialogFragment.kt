package com.example.socketchat.fragment

import android.app.Dialog
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
import androidx.lifecycle.viewModelScope
import com.example.socketchat.R
import com.example.socketchat.databinding.FragmentCreateCustomDialogBinding
import com.example.socketchat.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch

class CreatePartyDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentCreateCustomDialogBinding
    private val summaryViewModel: SummaryViewModel by activityViewModels()
    private lateinit var partyListFragment: PartyListFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateCustomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partyListFragment = parentFragment as PartyListFragment

        binding.btnCreateCheck.setOnClickListener {
            val memNo = requireArguments().getInt("currentUserMemNo", 0)
            val mainPhotoUrl = binding.editCreatePhotoUrl.text.toString()
            val title = binding.txtCreateTitle.text.toString()
            val maxMemberCountText = binding.editCreateMemberCount.text.toString()
            val isAutoJoin = binding.radioButtonCreateTure.isChecked

            val questContent = binding.editCreateQuestContent.text.toString()

            if(title.length < 5) {
                Toast.makeText(requireContext(), "5글자 이상으로 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (maxMemberCountText.isBlank() || maxMemberCountText.toInt() < 2) {
                Toast.makeText(requireContext(), "최소 2명 이상으로 인원 수를 입력해주세요" , Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val maxMemberCount = maxMemberCountText.toInt()

            if (questContent.length < 30) {
                Toast.makeText(requireContext(), "30글자 이상으로 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("CreateCustomDialog", "memNo: $memNo, mainPhotoUrl: $mainPhotoUrl, title: $title, maxMemberCount: $maxMemberCount, questContent: $questContent")


            lifecycleScope.launch {
                summaryViewModel.viewModelScope.launch {
                    summaryViewModel.fetchCreatePartyContext(memNo, mainPhotoUrl, title, maxMemberCount, isAutoJoin, questContent)
                    Log.d("CreatePartyDialogFragment", "After setCreateRoomRequest")
                }
            }

            dismiss()
        }

        binding.btnCreateCancel.setOnClickListener {
            dismiss()
        }
    }

    //다이얼로그의 형태를 정하는 부분
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }


}