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
import com.mycompany.mycapp.datos.Receta
import com.mycompany.mycapp.ui.theme.Texto

@Composable
fun RecetasPantalla(alSalir: () -> Unit) {
    MarcoPantalla(titulo = "Recetas", alSalir = alSalir) {
        Cargador(cargar = { Api.servicio.recetas() }) { recetas ->
            var busqueda by remember { mutableStateOf("") }
            val filtradas = remember(recetas, busqueda) {
                if (busqueda.isBlank()) recetas else recetas.filter { coincide(it, busqueda) }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BarraBusqueda(busqueda, { busqueda = it }, "Buscar por nombre o ingrediente")

                if (recetas.isEmpty()) {
                    Aviso("No hay recetas registradas.")
                } else if (filtradas.isEmpty()) {
                    Aviso("Sin resultados para \"$busqueda\".")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtradas, key = { it.idRecetas }) { receta ->
                            Tarjeta {
                                Text(
                                    text = receta.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Texto
                                )
                                Dato("Descripción:", receta.descripcion)

                                Text(
                                    text = "Ingredientes",
                                    fontWeight = FontWeight.Bold,
                                    color = Texto,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (receta.ingredientes.isEmpty()) {
                                    Dato("", "Esta receta todavía no tiene ingredientes cargados.")
                                } else {
                                    receta.ingredientes.forEach { ingrediente ->
                                        Dato("  •", "${ingrediente.nombre} — cantidad: ${ingrediente.cantUso}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun coincide(receta: Receta, busqueda: String) =
    receta.nombre.contains(busqueda, ignoreCase = true) ||
        receta.descripcion.contains(busqueda, ignoreCase = true) ||
        receta.ingredientes.any { it.nombre.contains(busqueda, ignoreCase = true) }
