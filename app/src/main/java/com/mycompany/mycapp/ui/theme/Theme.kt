package com.mycompany.mycapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Un solo esquema de color: el sistema de escritorio tiene una sola apariencia,
// asi que no se sigue el modo oscuro del telefono ni el color dinamico de Android 12+.
private val EsquemaMyC = lightColorScheme(
    primary = Acento,
    onPrimary = Color.White,
    secondary = BotonNormal,
    onSecondary = Texto,
    secondaryContainer = BotonNormal,
    onSecondaryContainer = Texto,
    background = FondoGeneral,
    onBackground = Texto,
    surface = FondoCard,
    onSurface = Texto,
    surfaceVariant = BotonNormal,
    onSurfaceVariant = Texto,
    outline = Borde,
    error = Acento,
    onError = Color.White
)

@Composable
fun MyCAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaMyC,
        typography = Typography,
        content = content
    )
}
