package com.example.socketchat.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private const val BASE_URL = "http://61.80.148.23:3000"

    fun retrofit(): SummaryUserInfoInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SummaryUserInfoInterface::class.java)
    }
}
