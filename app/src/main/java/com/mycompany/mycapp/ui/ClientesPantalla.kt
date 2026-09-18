package com.mycompany.mycapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mycompany.mycapp.datos.Api
import com.mycompany.mycapp.ui.theme.Texto

@Composable
fun ClientesPantalla(alSalir: () -> Unit) {
    MarcoPantalla(titulo = "Clientes", alSalir = alSalir) {
        Cargador(cargar = { Api.servicio.clientes() }) { clientes ->
            if (clientes.isEmpty()) {
                Aviso("No hay clientes registrados.")
                return@Cargador
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(clientes, key = { it.ruc }) { cliente ->
                    Tarjeta {
                        Text(
                            text = "${cliente.nombre} ${cliente.apellido}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Texto
                        )
                        Dato("RUC:", cliente.ruc)
                        Dato("Teléfono:", cliente.telefono)
                        Dato("Dirección:", cliente.direccion)
                    }
                }
            }
        }
    }
}
