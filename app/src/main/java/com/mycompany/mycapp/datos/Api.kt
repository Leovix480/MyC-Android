package com.mycompany.mycapp.datos

import com.mycompany.mycapp.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

data class Cliente(
    val ruc: String,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val direccion: String
)

data class Pedido(
    val idVenta: Int,
    val fecha: String,
    val ruc: String,
    val cliente: String,
    val productos: String,
    val total: Double,
    val tipoPago: String
)

data class Ingrediente(
    val idIngredientes: Int,
    val nombre: String,
    val stockIngredientes: Int,
    val precioIngredientes: Double,
    val stockMinimo: Int,
    val faltante: Boolean
)

data class IngredienteDeReceta(
    val nombre: String,
    val cantUso: Int
)

data class Receta(
    val idRecetas: Int,
    val nombre: String,
    val descripcion: String,
    val ingredientes: List<IngredienteDeReceta>
)

interface ServicioMyC {
    @GET("api/clientes")
    suspend fun clientes(): List<Cliente>

    @GET("api/pedidos")
    suspend fun pedidos(): List<Pedido>

    @GET("api/ingredientes")
    suspend fun ingredientes(): List<Ingrediente>

    @GET("api/recetas")
    suspend fun recetas(): List<Receta>
}

object Api {

    /** Se comparte con la descarga de PDF para que tambien mande la clave. */
    val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { cadena ->
                val peticion = cadena.request().newBuilder()
                    .addHeader("X-API-Key", Config.CLAVE_API)
                    .build()
                cadena.proceed(peticion)
            }
            .build()
    }

    val servicio: ServicioMyC by lazy {
        Retrofit.Builder()
            .baseUrl(Config.URL_BASE)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioMyC::class.java)
    }
}
