package com.partner.cinepulse.di

import com.partner.cinepulse.data.remote.apis.authApiService
import com.partner.cinepulse.data.remote.apis.contentApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object appModule{

    @Provides
    @Singleton
    fun getHttpClient() : OkHttpClient =
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://cinepulse-backend-7teu.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun getAuthAPI(retrofit: Retrofit): authApiService =
        retrofit.create(authApiService::class.java)

    @Provides
    @Singleton
    fun getContentAPI(retrofit: Retrofit) : contentApiService =
        retrofit.create(contentApiService::class.java)
}