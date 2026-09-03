package com.example.proyectoseminario.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.proyectoseminario.data.local.AppDao
import com.example.proyectoseminario.data.local.SincronizacionPendiente
import com.example.proyectoseminario.data.remote.ApiClient
import com.example.proyectoseminario.data.remote.RemoteNodo
import com.example.proyectoseminario.data.remote.RemoteRespuesta
import com.example.proyectoseminario.data.remote.SyncRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull

class SyncManager(
    private val context: Context,
    private val appDao: AppDao
) {
    private val syncApi = ApiClient.syncApi
    private val gson = Gson()

    fun hayConexion(): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivity.activeNetwork ?: return false
        val capabilities = connectivity.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun encolarRespuesta(correo: String, respuesta: RemoteRespuesta) {
        val json = gson.toJson(respuesta)
        val item = SincronizacionPendiente(
            correo = correo,
            tipo = "respuesta",
            datosJson = json
        )
        appDao.insertarSincronizacionPendiente(item)
    }

    suspend fun encolarNodo(correo: String, nodo: RemoteNodo) {
        val json = gson.toJson(nodo)
        val item = SincronizacionPendiente(
            correo = correo,
            tipo = "nodo",
            datosJson = json
        )
        appDao.insertarSincronizacionPendiente(item)
    }

    suspend fun sincronizar(correo: String): Result<String> {
        if (!hayConexion()) {
            return Result.failure(Exception("No hay conexión a internet"))
        }

        val pendientes = appDao.obtenerSincronizacionPendiente(correo)
        if (pendientes.isEmpty()) {
            return Result.success("No hay cambios pendientes")
        }

        val respuestas = mutableListOf<RemoteRespuesta>()
        val nodos = mutableListOf<RemoteNodo>()

        pendientes.forEach { item ->
            when (item.tipo) {
                "respuesta" -> {
                    val respuesta = gson.fromJson(item.datosJson, RemoteRespuesta::class.java)
                    respuestas.add(respuesta)
                }
                "nodo" -> {
                    val nodo = gson.fromJson(item.datosJson, RemoteNodo::class.java)
                    nodos.add(nodo)
                }
            }
        }

        return try {
            val request = SyncRequest(correo = correo, respuestas = respuestas, nodos = nodos)
            val response = syncApi.uploadProgreso(request)
            if (response.isSuccessful && response.body()?.success == true) {
                appDao.eliminarSincronizaciones(pendientes.map { it.id })
                Result.success("Sincronización exitosa")
            } else {
                Result.failure(Exception("Error del servidor: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarProgreso(correo: String): Result<com.example.proyectoseminario.data.remote.ProgresoRemoto?> {
        if (!hayConexion()) {
            return Result.failure(Exception("No hay conexión a internet"))
        }

        return try {
            val response = syncApi.fetchProgreso(correo)
            if (response.isSuccessful) {
                Result.success(response.body()?.progresoRemoto)
            } else {
                Result.failure(Exception("Error al descargar progreso"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fusionarPendientes(correo: String): List<SincronizacionPendiente> {
        return appDao.obtenerSincronizacionPendiente(correo)
    }
}
