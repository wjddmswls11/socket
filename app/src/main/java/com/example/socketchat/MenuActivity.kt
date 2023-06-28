package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.socketchat.data.SummaryUserInfo
import com.example.socketchat.databinding.ActivityMenuBinding
import com.example.socketchat.fragment.AlarmFragment
import com.example.socketchat.fragment.FriendFragment
import com.example.socketchat.fragment.PartyListFragment
import com.example.socketchat.viewmodel.MenuViewModel

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val viewModel : MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val summaryUserInfo = intent.getParcelableExtra<SummaryUserInfo>("SummaryUserInfo")

        if (summaryUserInfo != null) {
            initBottomNavigation(summaryUserInfo)
        }


        viewModel.setupParty()


        //바텀네비게이션 보라색으로 나오던 것
        binding.mainBtn.itemIconTintList = null
    }

    //바텀내비 초기 설정
    private fun initBottomNavigation(summaryUserInfo: SummaryUserInfo) {
        replaceFragment(FriendFragment().apply {
            arguments = bundleOf("summaryUserInfo" to summaryUserInfo)
        })

        binding.mainBtn.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu1 -> {
                    replaceFragment(FriendFragment().apply {
                        arguments = bundleOf("summaryUserInfo" to summaryUserInfo)
                    })
                    true
                }

                R.id.menu2 -> {
                    replaceFragment(PartyListFragment().apply {
                        arguments = bundleOf("summaryUserInfo" to summaryUserInfo)
                    })
                    true
                }

                R.id.menu3 -> {
                    replaceFragment(AlarmFragment().apply {
                        arguments = bundleOf("summaryUserInfo" to summaryUserInfo)
                    })
                    true
                }

                else -> false
            }
        }

    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }


}