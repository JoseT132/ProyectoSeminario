package com.example.proyectoseminario.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<AuthResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<SimpleResponse>

    @POST("api/auth/oauth")
    suspend fun oauthLogin(@Body request: OAuthRequest): Response<AuthResponse>
}

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val edad: Int,
    val nivelEscolar: String
)

data class LoginRequest(
    val correo: String,
    val password: String
)

data class RefreshRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class ForgotPasswordRequest(
    val correo: String
)

data class OAuthRequest(
    val provider: String,
    val token: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    val usuario: RemoteUsuario?
)

data class SimpleResponse(
    val success: Boolean,
    val message: String?
)

data class RemoteUsuario(
    val id: Int,
    val nombre: String,
    val correo: String?,
    val edad: Int?,
    val nivelEscolar: String,
    val puntos: Int,
    val rachaDias: Int
)
