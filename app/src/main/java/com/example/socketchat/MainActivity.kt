package com.example.socketchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.socketchat.databinding.ActivityMainBinding
import com.example.socketchat.request.SocketRequestManager
import com.example.socketchat.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private val socketRequestManager: SocketRequestManager by lazy { SocketRequestManager() }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //SocketRequestManager에 memNo를 넘긴다
        binding.btnMemNo.setOnClickListener {
            val memNo = binding.editTextMemNo.text.toString().trim()
            if (memNo.isEmpty()) {
                Toast.makeText(this@MainActivity, "회원 번호를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                socketRequestManager.sendAuthRequest(memNo.toInt())
            }
        }


        //result에 따라 인텐트와 토스트
        lifecycleScope.launch {
            viewModel.reAuthUserFlow.collect { reAuthUsers ->
                Log.d("MainActivity", "$reAuthUsers")
                when (reAuthUsers.data.result) {
                    1 -> {
                        val intent = Intent(this@MainActivity, MenuActivity::class.java).apply {
                            putExtra("NICKNAME", reAuthUsers.data.summaryUserInfo.nickName)
                            putExtra("MEMNO", reAuthUsers.data.summaryUserInfo.memNo)
                            putExtra(
                                "MAINPROFILEURL",
                                reAuthUsers.data.summaryUserInfo.mainProfileUrl
                            )
                        }
                        startActivity(intent)
                    }

                    2 -> {
                        Toast.makeText(this@MainActivity, "회원정보가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    3 -> {
                        Toast.makeText(this@MainActivity, "강제탈퇴 회원입니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    4 -> {
                        Toast.makeText(
                            this@MainActivity,
                            "알 수 없는 이유로 로그인 되지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Toast.makeText(this@MainActivity, "오류입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}
