package com.example.socketchat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socketchat.MenuActivity
import com.example.socketchat.adapter.AlarmAdapter
import com.example.socketchat.databinding.FragmentAlarmBinding
import com.example.socketchat.socket.SocketDataRepository
import kotlinx.coroutines.launch

class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private lateinit var alarmAdapter: AlarmAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        alarmAdapter = AlarmAdapter(requireActivity() as MenuActivity)

        viewLifecycleOwner.lifecycleScope.launch {
            SocketDataRepository.ntJoinPartyFlow.collect { ntJoinPartyFlow ->
                alarmAdapter.updateData(ntJoinPartyFlow)
            }
        }

        binding.alarmRcl001.adapter = alarmAdapter
        binding.alarmRcl001.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        return binding.root
    }


}