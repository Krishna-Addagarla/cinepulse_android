package com.partner.cinepulse.di

import com.partner.cinepulse.data.remote.apis.authApiService
import com.partner.cinepulse.data.remote.apis.contentApiService
import com.partner.cinepulse.data.remote.apis.fanClubApiService
import com.partner.cinepulse.data.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object appModule {

    // ===== AUTH CLIENT (No AuthInterceptor - for token refresh) =====
    @Provides
    @Singleton
    @Named("auth_client")
    fun getAuthOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @Named("auth_retrofit")
    fun getAuthRetrofit(@Named("auth_client") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://cinepulse-backend-7teu.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun getAuthAPI(@Named("auth_retrofit") retrofit: Retrofit): authApiService =
        retrofit.create(authApiService::class.java)

    // ===== MAIN CLIENT (With AuthInterceptor - for all other APIs) =====
    @Provides
    @Singleton
    fun getAuthInterceptor(
        tokenRepository: TokenRepository,
        authApiService: authApiService  // Now gets the auth_retrofit version
    ): AuthInterceptor {
        return AuthInterceptor(
            tokenRepository = tokenRepository,
            authApiService = authApiService
        )
    }

    @Provides
    @Singleton
    @Named("main_client")
    fun getMainHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun getRetrofit(@Named("main_client") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://cinepulse-backend-7teu.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    // ===== MAIN API SERVICES =====
    @Provides
    @Singleton
    fun getContentAPI(retrofit: Retrofit): contentApiService =
        retrofit.create(contentApiService::class.java)

    @Provides
    @Singleton
    fun getFanClubAPI(retrofit: Retrofit) : fanClubApiService =
        retrofit.create(fanClubApiService::class.java)
}