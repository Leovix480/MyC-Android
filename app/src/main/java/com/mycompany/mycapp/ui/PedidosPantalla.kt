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
import com.mycompany.mycapp.ui.theme.Texto

@Composable
fun PedidosPantalla(alSalir: () -> Unit) {
    val descargar = recordarDescarga()

    MarcoPantalla(
        titulo = "Pedidos realizados",
        alSalir = alSalir,
        botonesAccion = {
            BotonMyC("Descargar lista de pedidos (PDF)", Modifier.fillMaxWidth()) {
                descargar("api/reportes/pedidos", "pedidos.pdf")
            }
        }
    ) {
        Cargador(cargar = { Api.servicio.pedidos() }) { pedidos ->
            if (pedidos.isEmpty()) {
                Aviso("Todavía no hay pedidos registrados.")
                return@Cargador
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pedidos, key = { it.idVenta }) { pedido ->
                    Tarjeta {
                        Text(
                            text = "Pedido N° ${pedido.idVenta}  ·  ${pedido.fecha}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Texto
                        )
                        Dato("Cliente:", pedido.cliente)
                        Dato("Productos:", pedido.productos)
                        Dato("Forma de pago:", pedido.tipoPago)
                        Dato("Total:", guaranies(pedido.total))

                        BotonMyC("Factura PDF", Modifier.fillMaxWidth()) {
                            descargar(
                                "api/reportes/factura/${pedido.idVenta}",
                                "factura-${pedido.idVenta}.pdf"
                            )
                        }
                    }
                }
            }
        }
    }
}
