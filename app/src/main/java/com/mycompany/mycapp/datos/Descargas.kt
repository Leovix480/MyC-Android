package com.mycompany.mycapp.datos

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.mycompany.mycapp.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * Descarga un PDF generado por la API y lo deja disponible en el telefono.
 *
 * En Android 10 o superior va a la carpeta Descargas del sistema; en versiones
 * anteriores, a la carpeta de documentos de la app (no requiere permisos en
 * ninguno de los dos casos).
 */
object Descargas {

    suspend fun descargarPdf(contexto: Context, rutaApi: String, nombreArchivo: String): Uri =
        withContext(Dispatchers.IO) {
            val url = Config.URL_BASE.trimEnd('/') + "/" + rutaApi.trimStart('/')
            val peticion = Request.Builder().url(url).build()

            Api.http.newCall(peticion).execute().use { respuesta ->
                if (!respuesta.isSuccessful) {
                    throw IOException("El servidor respondio ${respuesta.code}")
                }
                val contenido = respuesta.body?.bytes()
                    ?: throw IOException("El servidor no devolvio ningun archivo")
                guardar(contexto, nombreArchivo, contenido)
            }
        }

    private fun guardar(contexto: Context, nombreArchivo: String, contenido: ByteArray): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val datos = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolucion = contexto.contentResolver
            val destino = resolucion.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, datos)
                ?: throw IOException("No se pudo crear el archivo en Descargas")

            resolucion.openOutputStream(destino)?.use { it.write(contenido) }
                ?: throw IOException("No se pudo escribir el archivo en Descargas")

            datos.clear()
            datos.put(MediaStore.Downloads.IS_PENDING, 0)
            resolucion.update(destino, datos, null, null)
            return destino
        }

        val carpeta = contexto.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: throw IOException("No hay almacenamiento disponible")
        carpeta.mkdirs()
        val archivo = File(carpeta, nombreArchivo)
        archivo.writeBytes(contenido)
        return FileProvider.getUriForFile(contexto, "${contexto.packageName}.archivos", archivo)
    }

    /** Abre el PDF con el visor que tenga instalado el telefono. */
    fun abrir(contexto: Context, archivo: Uri) {
        val intencion = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(archivo, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        contexto.startActivity(Intent.createChooser(intencion, "Abrir PDF"))
    }
}
