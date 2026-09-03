package com.example.proyectoseminario.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SyncApiService {

    @GET("api/sync/progreso")
    suspend fun fetchProgreso(@Query("correo") correo: String): Response<SyncResponse>

    @POST("api/sync/progreso")
    suspend fun uploadProgreso(@Body request: SyncRequest): Response<SyncResponse>
}

data class SyncRequest(
    val correo: String,
    @SerializedName("respuestas")
    val respuestas: List<RemoteRespuesta>,
    @SerializedName("nodos")
    val nodos: List<RemoteNodo>
)

data class SyncResponse(
    val success: Boolean,
    val message: String?,
    @SerializedName("progreso_remoto")
    val progresoRemoto: ProgresoRemoto?
)

data class ProgresoRemoto(
    @SerializedName("puntos")
    val puntos: Int,
    @SerializedName("racha_dias")
    val rachaDias: Int,
    @SerializedName("nodos_completados")
    val nodosCompletados: List<Int>,
    @SerializedName("respuestas")
    val respuestas: List<RemoteRespuesta>
)

data class RemoteRespuesta(
    @SerializedName("ejercicio_id")
    val ejercicioId: Int,
    @SerializedName("nodo_id")
    val nodoId: Int,
    @SerializedName("es_correcto")
    val esCorrecto: Boolean,
    @SerializedName("tiempo_segundos")
    val tiempoSegundos: Int,
    @SerializedName("fecha_timestamp")
    val fechaTimestamp: Long
)

data class RemoteNodo(
    val id: Int,
    @SerializedName("esta_completado")
    val estaCompletado: Boolean,
    @SerializedName("esta_desbloqueado")
    val estaDesbloqueado: Boolean
)
