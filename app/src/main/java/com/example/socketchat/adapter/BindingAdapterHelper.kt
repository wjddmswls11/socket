package com.example.socketchat.adapter

import android.icu.util.Calendar
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.socketchat.R
import java.text.SimpleDateFormat
import java.util.Locale


object BindingAdapterHelper {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
            .load(url)
            .transform(CircleCrop())
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("messageText", "isDeleted")
    fun setTextWithIsDeleted(textView: TextView, messageText: String?, isDeleted: Boolean) {
        if (isDeleted) {
            textView.text = "삭제된 메시지입니다."
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_report_blue, 0, 0, 0
            )
        } else {
            textView.text = messageText ?: ""
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }


    //타임 스탬프를 시간 형식으로 변환하는 함수입니다.
    fun convertTimestampToTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    @JvmStatic
    @BindingAdapter("timestamp")
    fun setConvertedTimestamp(textView: TextView, timestamp: Long?) {
        val time = timestamp?.let { convertTimestampToTime(it) }
        textView.text = time
    }




}