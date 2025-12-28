package com.lvmh.pocketpet.presentacion.tema

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaClaro = lightColorScheme(
    // Primarios (Morado)
    primary = MoradoPrincipal,
    onPrimary = Color.White,
    primaryContainer = MoradoClaro,
    onPrimaryContainer = GrisTexto,

    // Secundarios (Verde menta)
    secondary = VerdeMenta,
    onSecondary = Color.White,
    secondaryContainer = VerdeMentaClaro,
    onSecondaryContainer = GrisTexto,

    // Terciarios (Rosa pastel)
    tertiary = RosaPastel,
    onTertiary = Color.White,
    tertiaryContainer = RosaPastelClaro,
    onTertiaryContainer = GrisTexto,

    // Fondos
    background = FondoApp,
    onBackground = GrisTexto,

    // Superficies
    surface = FondoBlanco,
    onSurface = GrisTexto,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = GrisMedio,

    // Estados
    error = CoralPastel,
    onError = Color.White,
    errorContainer = DuraznoClaro,
    onErrorContainer = GrisTexto,

    // Contornos
    outline = GrisClaro,
    outlineVariant = GrisClaro.copy(alpha = 0.5f),

    // Inversas
    inverseSurface = GrisOscuro,
    inverseOnSurface = FondoBlanco,
    inversePrimary = MoradoClaro
)

private val EsquemaOscuro = darkColorScheme(
    // Primarios
    primary = MoradoClaro,
    onPrimary = GrisOscuro,
    primaryContainer = MoradoPrincipal,
    onPrimaryContainer = Color.White,

    // Secundarios
    secondary = VerdeMentaClaro,
    onSecondary = GrisOscuro,
    secondaryContainer = VerdeMenta,
    onSecondaryContainer = Color.White,

    // Terciarios
    tertiary = RosaPastelClaro,
    onTertiary = GrisOscuro,
    tertiaryContainer = RosaPastel,
    onTertiaryContainer = Color.White,

    // Fondos
    background = FondoOscuro,
    onBackground = FondoClaro,

    // Superficies
    surface = SuperficieOscura,
    onSurface = FondoClaro,
    surfaceVariant = GrisOscuro,
    onSurfaceVariant = GrisClaro,

    // Estados
    error = RojoSecundario,
    onError = GrisOscuro,
    errorContainer = RojoPrimario,
    onErrorContainer = Color.White,

    // Contornos
    outline = GrisMedio,
    outlineVariant = GrisMedio.copy(alpha = 0.5f),

    // Inversas
    inverseSurface = FondoClaro,
    inverseOnSurface = GrisOscuro,
    inversePrimary = MoradoPrincipal
)

@Composable
fun PocketPetTema(
    modoOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val esquemaColor = if (modoOscuro) EsquemaOscuro else EsquemaClaro

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            ventana.statusBarColor = esquemaColor.primary.toArgb()
            WindowCompat.getInsetsController(ventana, vista)
                .isAppearanceLightStatusBars = !modoOscuro
        }
    }

    MaterialTheme(
        colorScheme = esquemaColor,
        typography = Tipografia,
        shapes = Formas,
        content = contenido
    )
}

@Composable
fun MascotaFinancieraTheme(
    modoOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    PocketPetTema(modoOscuro, contenido)
}
