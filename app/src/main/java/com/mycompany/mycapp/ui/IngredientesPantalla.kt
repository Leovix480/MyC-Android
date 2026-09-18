package com.mycompany.mycapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mycompany.mycapp.ui.theme.Acento
import com.mycompany.mycapp.ui.theme.Texto

@Composable
fun IngredientesPantalla(alSalir: () -> Unit) {
    val descargar = recordarDescarga()

    MarcoPantalla(
        titulo = "Ingredientes",
        alSalir = alSalir,
        botonesAccion = {
            BotonMyC("Descargar ingredientes faltantes (PDF)", Modifier.fillMaxWidth()) {
                descargar("api/reportes/ingredientes-faltantes", "ingredientes-faltantes.pdf")
            }
        }
    ) {
        Cargador(cargar = { Api.servicio.ingredientes() }) { ingredientes ->
            var busqueda by remember { mutableStateOf("") }
            val filtrados = remember(ingredientes, busqueda) {
                if (busqueda.isBlank()) ingredientes
                else ingredientes.filter { it.nombre.contains(busqueda, ignoreCase = true) }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BarraBusqueda(busqueda, { busqueda = it }, "Buscar ingrediente")

                if (ingredientes.isEmpty()) {
                    Aviso("No hay ingredientes registrados.")
                } else if (filtrados.isEmpty()) {
                    Aviso("Sin resultados para \"$busqueda\".")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtrados, key = { it.idIngredientes }) { ingrediente ->
                            Tarjeta {
                                Text(
                                    text = ingrediente.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Texto
                                )
                                Dato("Stock actual:", ingrediente.stockIngredientes.toString())
                                Dato("Stock mínimo:", ingrediente.stockMinimo.toString())
                                Dato("Precio:", guaranies(ingrediente.precioIngredientes))

                                if (ingrediente.faltante) {
                                    Text(
                                        text = "Por debajo del stock mínimo",
                                        color = Acento,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
