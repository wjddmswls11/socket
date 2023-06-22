package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.socketchat.databinding.ActivityMenuBinding
import com.example.socketchat.fragment.AlarmFragment
import com.example.socketchat.fragment.FriendFragment
import com.example.socketchat.fragment.PartyListFragment
import com.example.socketchat.viewmodel.MenuViewModel

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val viewModel : MenuViewModel by viewModels()

    var currentUserMemNo: Int = -1
    var currentUserNickName: String = ""
    var mainProfileUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserMemNo = intent.getIntExtra("MEMNO", -1)
        currentUserNickName = intent.getStringExtra("NICKNAME") ?: ""
        mainProfileUrl = intent.getStringExtra("MAINPROFILEURL") ?: ""

        viewModel.setupParty()

        initBottomNavigation()

        //바텀네비게이션 보라색으로 나오던 것
        binding.mainBtn.itemIconTintList = null



    }

    //바텀내비 초기 설정
    private fun initBottomNavigation() {
        replaceFragment(FriendFragment().apply {
            arguments = bundleOf(
                "currentUserMemNo" to currentUserMemNo,
                "currentUserNickName" to currentUserNickName,
                "mainProfileUrl" to mainProfileUrl
            )
        })

        binding.mainBtn.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu1 -> {
                    replaceFragment(FriendFragment().apply {
                        arguments = bundleOf(
                            "currentUserMemNo" to currentUserMemNo,
                            "currentUserNickName" to currentUserNickName,
                            "mainProfileUrl" to mainProfileUrl
                        )
                    })
                    true
                }

                R.id.menu2 -> {
                    replaceFragment(PartyListFragment().apply {
                        arguments = bundleOf("currentUserMemNo" to currentUserMemNo)
                    })
                    true
                }

                R.id.menu3 -> {
                    replaceFragment(AlarmFragment().apply {
                        arguments = bundleOf("currentUserMemNo" to currentUserMemNo)
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