package com.mycompany.mycapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.mycompany.mycapp.ui.theme.Acento
import com.mycompany.mycapp.ui.theme.Borde
import com.mycompany.mycapp.ui.theme.BotonNormal
import com.mycompany.mycapp.ui.theme.FondoCard
import com.mycompany.mycapp.ui.theme.FondoGeneral
import com.mycompany.mycapp.ui.theme.Texto
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// ---------------------------------------------------------------- estado de carga

sealed interface Estado<out T> {
    data object Cargando : Estado<Nothing>
    data class Error(val mensaje: String) : Estado<Nothing>
    data class Listo<T>(val datos: T) : Estado<T>
}

/**
 * Pide los datos a la API y muestra "cargando", el error con un boton de
 * reintentar, o el contenido. Ademas soporta "deslizar hacia abajo para
 * recargar" (como en Instagram o un navegador): si ya hay datos en pantalla,
 * un refresco fallido no los borra, solo avisa con un Toast y los deja como
 * estaban.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Cargador(
    cargar: suspend () -> T,
    contenido: @Composable (T) -> Unit
) {
    var intento by remember { mutableIntStateOf(0) }
    var refrescando by remember { mutableStateOf(false) }
    var estado by remember { mutableStateOf<Estado<T>>(Estado.Cargando) }
    val contexto = LocalContext.current

    LaunchedEffect(intento) {
        val esRefresco = estado is Estado.Listo
        if (!esRefresco) estado = Estado.Cargando
        try {
            val datos = cargar()
            estado = Estado.Listo(datos)
        } catch (e: Exception) {
            if (esRefresco) {
                Toast.makeText(contexto, mensajeDeError(e), Toast.LENGTH_LONG).show()
            } else {
                estado = Estado.Error(mensajeDeError(e))
            }
        } finally {
            refrescando = false
        }
    }

    PullToRefreshBox(
        isRefreshing = refrescando,
        onRefresh = { refrescando = true; intento++ },
        modifier = Modifier.fillMaxSize()
    ) {
        when (val actual = estado) {
            is Estado.Cargando -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Acento)
            }

            is Estado.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Tarjeta {
                    Text(
                        text = actual.mensaje,
                        color = Texto,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    BotonAcento("Reintentar", Modifier.fillMaxWidth()) { intento++ }
                }
            }

            is Estado.Listo -> contenido(actual.datos)
        }
    }
}

fun mensajeDeError(e: Throwable): String = when {
    e is UnknownHostException || e is ConnectException || e is SocketTimeoutException ->
        "No se pudo conectar con el servidor. Revisá la conexión a internet y la dirección configurada en Config.kt."

    e is HttpException && e.code() == 401 ->
        "El servidor rechazó la clave de API. Revisá que CLAVE_API coincida con la del servidor."

    e is HttpException ->
        "El servidor respondió con un error ${e.code()}."

    else -> e.message ?: "Ocurrió un error inesperado."
}

// ---------------------------------------------------------------- piezas visuales

/** Encabezado + contenido scrolleable + botones abajo, con el "Salir" siempre presente. */
@Composable
fun MarcoPantalla(
    titulo: String,
    alSalir: () -> Unit,
    botonesAccion: @Composable ColumnScope.() -> Unit = {},
    contenido: @Composable () -> Unit
) {
    Scaffold(containerColor = FondoGeneral) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Texto
            )

            Box(Modifier.weight(1f)) { contenido() }

            botonesAccion()
            BotonAcento("Salir", Modifier.fillMaxWidth(), alTocar = alSalir)
        }
    }
}

/** Equivalente a la clase .card de estilos.css */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = FondoCard,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, Borde)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = contenido
        )
    }
}

/** Equivalente a .boton */
@Composable
fun BotonMyC(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    alTocar: () -> Unit
) {
    Button(
        onClick = alTocar,
        modifier = modifier,
        enabled = habilitado,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Borde),
        colors = ButtonDefaults.buttonColors(containerColor = BotonNormal, contentColor = Texto)
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

/** Equivalente a .boton-acento */
@Composable
fun BotonAcento(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    alTocar: () -> Unit
) {
    Button(
        onClick = alTocar,
        modifier = modifier,
        enabled = habilitado,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Texto),
        colors = ButtonDefaults.buttonColors(containerColor = Acento, contentColor = Color.White)
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

/** Mensaje centrado para cuando una vista no tiene datos que mostrar. */
@Composable
fun Aviso(mensaje: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Tarjeta {
            Text(
                text = mensaje,
                color = Texto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Barra de busqueda para filtrar listas (Pedidos, Clientes, Ingredientes, Recetas). */
@Composable
fun BarraBusqueda(
    valor: String,
    alCambiar: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (valor.isNotEmpty()) {
                IconButton(onClick = { alCambiar("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar busqueda")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {}),
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FondoGeneral,
            unfocusedContainerColor = FondoGeneral,
            focusedBorderColor = Borde,
            unfocusedBorderColor = Borde,
            cursorColor = Texto,
            focusedTextColor = Texto,
            unfocusedTextColor = Texto,
            focusedPlaceholderColor = Texto.copy(alpha = 0.5f),
            unfocusedPlaceholderColor = Texto.copy(alpha = 0.5f),
            focusedLeadingIconColor = Texto,
            unfocusedLeadingIconColor = Texto,
            focusedTrailingIconColor = Texto,
            unfocusedTrailingIconColor = Texto
        )
    )
}

/** Fila "etiqueta: valor" como las de las vistas del escritorio. */
@Composable
fun Dato(etiqueta: String, valor: String) {
    Text(
        text = "$etiqueta $valor",
        color = Texto,
        style = MaterialTheme.typography.bodyMedium
    )
}

// ---------------------------------------------------------------- formato de montos

private val formatoMonto = DecimalFormat(
    "#,##0.00",
    DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
)

fun guaranies(monto: Double): String = "Gs. " + formatoMonto.format(monto)
