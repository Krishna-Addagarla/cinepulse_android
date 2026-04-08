package com.partner.cinepulse.data.repositoryImpl

//import android.net.http.HttpException
import retrofit2.HttpException
import com.partner.cinepulse.data.remote.apis.authApiService
import com.partner.cinepulse.data.remote.models.loginRequest
import com.partner.cinepulse.data.remote.models.otpVerificationRequest
import com.partner.cinepulse.data.remote.models.registrationRequest
import com.partner.cinepulse.data.remote.models.registrationResponse
import com.partner.cinepulse.data.remote.models.resendResponse
import com.partner.cinepulse.data.remote.models.verifyResponse
import com.partner.cinepulse.data.repository.AuthRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi : authApiService
) : AuthRepository {

    override suspend fun registerUser(request: registrationRequest): Flow<Resource<registrationResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = authApi.registerUser(request)
            if (response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()?:"Registration Failed"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "Registration failed"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.message}"))
        }
    }

    override suspend fun loginUser(request: loginRequest): Flow<Resource<verifyResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = authApi.loginUser(request)

            if (response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()?:"Login Failed"))
            }

        }catch (e: HttpException){
            emit(Resource.Error(e.message()?:"Login Failed"))
        }catch (e: IOException){
            emit(Resource.Error("Network error: ${e.message}"))
        }catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.message}"))
        }

    }

    override suspend fun verifyOTP(request: otpVerificationRequest): Flow<Resource<verifyResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = authApi.verifyOTP(request)
            if(response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()?:"Verify Failed"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message()?:"Verify Failed"))
        }catch (e: IOException){
            emit(Resource.Error("Network error: ${e.message}"))
        }catch (e: java.lang.Exception){
            emit(Resource.Error("Unexpected error: ${e.message}"))
        }


    }

    override suspend fun resendOTP(email : String): Flow<Resource<resendResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = authApi.resendOTP(email)

            if (response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()?:"Resending OTP Failed"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "Failed to resend OTP"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.message}"))
        }
    }
}