package com.example.socketchat.`object`

import android.app.Activity
import android.app.AlertDialog
import android.content.Context

object KickoutDialog {

    fun showKickOutDialog(context: Context, partyNo: Int, partyChatActivity: Activity) {
        val dialogBuilder = AlertDialog.Builder(context)
            .setMessage("당신은 $partyNo 번 방에서 강퇴되었습니다.")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
                partyChatActivity.finish()
            }
        val kickOutDialog = dialogBuilder.create()
        kickOutDialog.show()
    }


}