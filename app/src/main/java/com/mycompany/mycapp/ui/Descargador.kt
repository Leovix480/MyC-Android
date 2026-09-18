package com.mycompany.mycapp.ui

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.mycompany.mycapp.datos.Descargas
import kotlinx.coroutines.launch

/**
 * Devuelve una funcion que descarga un PDF de la API y lo abre.
 * Se usa desde los botones de las vistas: descargar("api/reportes/pedidos", "pedidos.pdf")
 */
@Composable
fun recordarDescarga(): (String, String) -> Unit {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    return { rutaApi, nombreArchivo ->
        Toast.makeText(contexto, "Descargando $nombreArchivo…", Toast.LENGTH_SHORT).show()
        alcance.launch {
            try {
                val archivo = Descargas.descargarPdf(contexto, rutaApi, nombreArchivo)
                Toast.makeText(contexto, "Descargado: $nombreArchivo", Toast.LENGTH_LONG).show()
                try {
                    Descargas.abrir(contexto, archivo)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(
                        contexto,
                        "El archivo quedó en Descargas, pero no hay ninguna app para abrir PDF.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(contexto, mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }
}
