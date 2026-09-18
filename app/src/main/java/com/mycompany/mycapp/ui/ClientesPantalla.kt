package com.mycompany.mycapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mycompany.mycapp.datos.Api
import com.mycompany.mycapp.datos.Cliente
import com.mycompany.mycapp.ui.theme.Texto

@Composable
fun ClientesPantalla(alSalir: () -> Unit) {
    MarcoPantalla(titulo = "Clientes", alSalir = alSalir) {
        Cargador(cargar = { Api.servicio.clientes() }) { clientes ->
            var busqueda by remember { mutableStateOf("") }
            val filtrados = remember(clientes, busqueda) {
                if (busqueda.isBlank()) clientes else clientes.filter { coincide(it, busqueda) }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BarraBusqueda(busqueda, { busqueda = it }, "Buscar por nombre, apellido o RUC")

                if (clientes.isEmpty()) {
                    Aviso("No hay clientes registrados.")
                } else if (filtrados.isEmpty()) {
                    Aviso("Sin resultados para \"$busqueda\".")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtrados, key = { it.ruc }) { cliente ->
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
    }
}

private fun coincide(cliente: Cliente, busqueda: String) =
    cliente.nombre.contains(busqueda, ignoreCase = true) ||
        cliente.apellido.contains(busqueda, ignoreCase = true) ||
        cliente.ruc.contains(busqueda, ignoreCase = true)
