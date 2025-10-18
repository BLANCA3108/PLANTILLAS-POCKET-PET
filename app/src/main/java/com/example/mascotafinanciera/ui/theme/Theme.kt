package com.example.mascotafinanciera.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaClaro = lightColorScheme(
    primary = MoradoPrincipal,
    secondary = VerdeMenta,
    tertiary = RosaPastel,
    background = FondoApp,
    surface = FondoBlanco,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = GrisTexto,
    onBackground = GrisTexto,
    onSurface = GrisTexto,
)

private val EsquemaOscuro = darkColorScheme(
    primary = MoradoClaro,
    secondary = VerdeMentaClaro,
    tertiary = RosaPastelClaro,
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF2D2D2D),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
)

@Composable
fun MascotaFinancieraTheme(
    modoOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val esquemaColores = if (modoOscuro) {
        EsquemaOscuro
    } else {
        EsquemaClaro
    }

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            ventana.statusBarColor = esquemaColores.primary.toArgb()
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !modoOscuro
        }
    }

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = Typography,
        content = contenido
    )
}