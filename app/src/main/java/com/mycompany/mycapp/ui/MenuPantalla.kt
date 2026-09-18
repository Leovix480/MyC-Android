package com.mycompany.mycapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mycompany.mycapp.R
import com.mycompany.mycapp.datos.Api
import com.mycompany.mycapp.ui.theme.Acento
import com.mycompany.mycapp.ui.theme.FondoCard
import com.mycompany.mycapp.ui.theme.FondoGeneral
import com.mycompany.mycapp.ui.theme.Texto

/**
 * Menu principal, con la misma distribucion que menu.fxml del escritorio:
 * logo arriba, alerta de stock, y los accesos a las vistas.
 */
@Composable
fun MenuPantalla(
    alAbrirPedidos: () -> Unit,
    alAbrirRecetas: () -> Unit,
    alAbrirIngredientes: () -> Unit,
    alAbrirClientes: () -> Unit,
    alCerrar: () -> Unit
) {
    Scaffold(containerColor = FondoGeneral) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.myc_logo),
                contentDescription = "Miel y Canela",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            AlertaStock()

            // Mismos accesos que el panel de navegacion del escritorio
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = FondoCard,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, com.mycompany.mycapp.ui.theme.Borde)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BotonMyC("Pedidos", Modifier.fillMaxWidth(), alTocar = alAbrirPedidos)
                    BotonMyC("Recetas", Modifier.fillMaxWidth(), alTocar = alAbrirRecetas)
                    BotonMyC("Ingredientes", Modifier.fillMaxWidth(), alTocar = alAbrirIngredientes)
                    BotonMyC("Clientes", Modifier.fillMaxWidth(), alTocar = alAbrirClientes)
                }
            }

            BotonAcento("Cerrar sesión", Modifier.fillMaxWidth(), alTocar = alCerrar)

            Text(
                text = "Miel y Canela · Consulta de datos",
                style = MaterialTheme.typography.bodySmall,
                color = Texto
            )
        }
    }
}

/**
 * Misma alerta que muestra el menu del escritorio cuando hay ingredientes por
 * debajo del stock minimo. Si la API no responde lo avisa, porque sin conexion
 * ninguna vista va a poder cargar.
 */
@Composable
private fun AlertaStock() {
    val texto by produceState<String?>(initialValue = null) {
        value = try {
            val faltantes = Api.servicio.ingredientes().count { it.faltante }
            when (faltantes) {
                0 -> null
                1 -> "Alerta: 1 ingrediente está por debajo del stock mínimo"
                else -> "Alerta: $faltantes ingredientes están por debajo del stock mínimo"
            }
        } catch (e: Exception) {
            "Sin conexión con el servidor. " + mensajeDeError(e)
        }
    }

    val mensaje = texto ?: return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FondoCard,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, Acento)
    ) {
        Text(
            text = mensaje,
            color = Texto,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}
