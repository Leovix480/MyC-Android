package com.mycompany.mycapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.mycompany.mycapp.ui.ClientesPantalla
import com.mycompany.mycapp.ui.IngredientesPantalla
import com.mycompany.mycapp.ui.MenuPantalla
import com.mycompany.mycapp.ui.PedidosPantalla
import com.mycompany.mycapp.ui.RecetasPantalla
import com.mycompany.mycapp.ui.theme.MyCAppTheme

private enum class Vista { MENU, PEDIDOS, RECETAS, INGREDIENTES, CLIENTES }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app siempre tiene fondo claro, asi que el reloj y los iconos del
        // sistema tienen que dibujarse oscuros para que se lean.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            MyCAppTheme {
                // Igual que en el escritorio: el menu es la base y cada vista se
                // abre encima; al salir de una vista se vuelve al menu.
                var vista by rememberSaveable { mutableStateOf(Vista.MENU) }
                val volverAlMenu = { vista = Vista.MENU }

                BackHandler(enabled = vista != Vista.MENU) { volverAlMenu() }

                when (vista) {
                    Vista.MENU -> MenuPantalla(
                        alAbrirPedidos = { vista = Vista.PEDIDOS },
                        alAbrirRecetas = { vista = Vista.RECETAS },
                        alAbrirIngredientes = { vista = Vista.INGREDIENTES },
                        alAbrirClientes = { vista = Vista.CLIENTES },
                        alCerrar = { finish() }
                    )

                    Vista.PEDIDOS -> PedidosPantalla(alSalir = volverAlMenu)
                    Vista.RECETAS -> RecetasPantalla(alSalir = volverAlMenu)
                    Vista.INGREDIENTES -> IngredientesPantalla(alSalir = volverAlMenu)
                    Vista.CLIENTES -> ClientesPantalla(alSalir = volverAlMenu)
                }
            }
        }
    }
}
