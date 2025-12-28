package com.lvmh.pocketpet.presentacion.tema

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaColorOscuro = darkColorScheme(
    primary = AzulPrimario,
    onPrimary = FondoClaro,
    primaryContainer = AzulPrimarioVariante,
    onPrimaryContainer = FondoClaro,
    secondary = AzulSecundario,
    onSecondary = FondoClaro,
    secondaryContainer = AzulSecundario,
    onSecondaryContainer = FondoClaro,
    tertiary = VerdePrimario,
    onTertiary = FondoClaro,
    error = RojoPrimario,
    onError = FondoClaro,
    background = FondoOscuro,
    onBackground = FondoClaro,
    surface = SuperficieOscura,
    onSurface = FondoClaro,
    surfaceVariant = GrisOscuro,
    onSurfaceVariant = GrisClaro
)

private val EsquemaColorClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = FondoClaro,
    primaryContainer = AzulPrimarioVariante,
    onPrimaryContainer = FondoClaro,
    secondary = AzulSecundario,
    onSecondary = FondoClaro,
    secondaryContainer = AzulSecundario,
    onSecondaryContainer = FondoClaro,
    tertiary = VerdePrimario,
    onTertiary = FondoClaro,
    error = RojoPrimario,
    onError = FondoClaro,
    background = FondoClaro,
    onBackground = GrisOscuro,
    surface = SuperficieClara,
    onSurface = GrisOscuro,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = GrisOscuro
)

@Composable
fun PocketPetTema(
    modoOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val esquemaColor = if (modoOscuro) EsquemaColorOscuro else EsquemaColorClaro

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            ventana.statusBarColor = esquemaColor.primary.toArgb()
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !modoOscuro
        }
    }

    MaterialTheme(
        colorScheme = esquemaColor,
        typography = Tipografia,
        shapes = Formas,
        content = contenido
    )
}