package com.mycompany.mycapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            if (ingredientes.isEmpty()) {
                Aviso("No hay ingredientes registrados.")
                return@Cargador
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ingredientes, key = { it.idIngredientes }) { ingrediente ->
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
