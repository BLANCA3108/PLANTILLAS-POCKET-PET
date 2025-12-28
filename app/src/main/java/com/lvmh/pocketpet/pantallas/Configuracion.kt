package com.lvmh.pocketpet.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.rotate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Configuracion(onBack: () -> Unit = {}, onNext: () -> Unit = {}) {
    // Colores cálidos rosado y morado suave
    val rosadoSuave = Color(0xFFFFB6C1)
    val moradoSuave = Color(0xFFE6B0FF)
    val rosadoClaro = Color(0xFFFFF0F5)
    val moradoClaro = Color(0xFFF8F0FF)

    // Estados
    var opcionSeleccionada by remember { mutableStateOf("App") }
    var showMonedaDialog by remember { mutableStateOf(false) }
    var showAyudaDialog by remember { mutableStateOf(false) }
    var showAcercaDialog by remember { mutableStateOf(false) }

    // Estados de configuración de App
    var notificaciones by remember { mutableStateOf(true) }
    var recordatorios by remember { mutableStateOf(true) }
    var monedaSeleccionada by remember { mutableStateOf("PEN - Sol Peruano") }

    // Estados de configuración de Mascota
    var recordatoriosCuidado by remember { mutableStateOf(true) }
    var sonidos by remember { mutableStateOf(true) }
    var animaciones by remember { mutableStateOf(true) }

    // Animación de entrada
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        rosadoClaro,
                        moradoClaro
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = CircleShape
                                    )
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = moradoSuave
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "⚙️ Configuración",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = moradoSuave
                    ),
                    modifier = Modifier.shadow(elevation = 4.dp)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .scale(scale)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Mascota animada
                MascotaConfiguracion()

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de pestaña
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabButton(
                        texto = "📱 App",
                        seleccionado = opcionSeleccionada == "App",
                        onClick = { opcionSeleccionada = "App" },
                        color = rosadoSuave
                    )
                    TabButton(
                        texto = "🐕 Mascota",
                        seleccionado = opcionSeleccionada == "Mascota",
                        onClick = { opcionSeleccionada = "Mascota" },
                        color = moradoSuave
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Contenido según pestaña seleccionada
                AnimatedVisibility(
                    visible = opcionSeleccionada == "App",
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    ContenidoAppMejorado(
                        notificaciones = notificaciones,
                        onNotificacionesChange = { notificaciones = it },
                        recordatorios = recordatorios,
                        onRecordatoriosChange = { recordatorios = it },
                        monedaSeleccionada = monedaSeleccionada,
                        onMonedaClick = { showMonedaDialog = true },
                        onAyudaClick = { showAyudaDialog = true },
                        onAcercaClick = { showAcercaDialog = true },
                        colorPrincipal = rosadoSuave
                    )
                }

                AnimatedVisibility(
                    visible = opcionSeleccionada == "Mascota",
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    ContenidoMascotaMejorado(
                        recordatoriosCuidado = recordatoriosCuidado,
                        onRecordatoriosChange = { recordatoriosCuidado = it },
                        sonidos = sonidos,
                        onSonidosChange = { sonidos = it },
                        animaciones = animaciones,
                        onAnimacionesChange = { animaciones = it },
                        colorPrincipal = moradoSuave
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Guardar
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (opcionSeleccionada == "App") rosadoSuave else moradoSuave
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Guardar Configuración",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Dialogs
        if (showMonedaDialog) {
            DialogSeleccionMoneda(
                monedaActual = monedaSeleccionada,
                onDismiss = { showMonedaDialog = false },
                onSeleccionar = {
                    monedaSeleccionada = it
                    showMonedaDialog = false
                },
                color = rosadoSuave
            )
        }

        if (showAyudaDialog) {
            DialogAyuda(
                onDismiss = { showAyudaDialog = false },
                color = rosadoSuave
            )
        }

        if (showAcercaDialog) {
            DialogAcerca(
                onDismiss = { showAcercaDialog = false },
                color = rosadoSuave
            )
        }
    }
}

/**
 * Mascota animada en la parte superior
 */
@Composable
private fun MascotaConfiguracion() {
    val infiniteTransition = rememberInfiniteTransition(label = "mascota")

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFB6C1),
                        Color(0xFFE6B0FF)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚙️",
                fontSize = 60.sp,
                modifier = Modifier.rotate(rotation)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Ajusta todo a",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "tu medida",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Botón de pestaña
 */
@Composable
private fun RowScope.TabButton(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) color else Color.Transparent,
            contentColor = if (seleccionado) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (seleccionado) 4.dp else 0.dp
        )
    ) {
        Text(
            texto,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

/**
 * Contenido de App mejorado
 */
@Composable
private fun ContenidoAppMejorado(
    notificaciones: Boolean,
    onNotificacionesChange: (Boolean) -> Unit,
    recordatorios: Boolean,
    onRecordatoriosChange: (Boolean) -> Unit,
    monedaSeleccionada: String,
    onMonedaClick: () -> Unit,
    onAyudaClick: () -> Unit,
    onAcercaClick: () -> Unit,
    colorPrincipal: Color
) {
    Column {
        // GENERAL
        SeccionHeaderConfig("GENERAL", Icons.Default.Settings)

        Spacer(modifier = Modifier.height(12.dp))

        ItemConfigSwitch(
            icono = Icons.Default.Notifications,
            titulo = "Notificaciones",
            descripcion = "Recibe alertas importantes",
            checked = notificaciones,
            onCheckedChange = onNotificacionesChange,
            colorSwitch = colorPrincipal
        )

        Spacer(modifier = Modifier.height(8.dp))

        ItemConfigSwitch(
            icono = Icons.Default.Notifications,
            titulo = "Recordatorios",
            descripcion = "Avisos de transacciones",
            checked = recordatorios,
            onCheckedChange = onRecordatoriosChange,
            colorSwitch = colorPrincipal
        )

        Spacer(modifier = Modifier.height(24.dp))

        // AYUDA Y SOPORTE
        SeccionHeaderConfig("AYUDA Y SOPORTE", Icons.Default.Info)

        Spacer(modifier = Modifier.height(12.dp))

        ItemConfigClickeable(
            icono = Icons.Default.Info,
            titulo = "Ayuda",
            descripcion = "Preguntas frecuentes",
            onClick = onAyudaClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        ItemConfigClickeable(
            icono = Icons.Default.Info,
            titulo = "Acerca de",
            descripcion = "Información de la app",
            onClick = onAcercaClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // MONEDA
        SeccionHeaderConfig("MONEDA", Icons.Default.LocationOn)

        Spacer(modifier = Modifier.height(12.dp))

        ItemConfigClickeable(
            icono = Icons.Default.LocationOn,
            titulo = "Moneda Predeterminada",
            descripcion = monedaSeleccionada,
            onClick = onMonedaClick
        )
    }
}

/**
 * Contenido de Mascota mejorado
 */
@Composable
private fun ContenidoMascotaMejorado(
    recordatoriosCuidado: Boolean,
    onRecordatoriosChange: (Boolean) -> Unit,
    sonidos: Boolean,
    onSonidosChange: (Boolean) -> Unit,
    animaciones: Boolean,
    onAnimacionesChange: (Boolean) -> Unit,
    colorPrincipal: Color
) {
    Column {
        // INTERACCIÓN
        SeccionHeaderConfig("INTERACCIÓN CON MASCOTA", Icons.Default.Favorite)

        Spacer(modifier = Modifier.height(12.dp))

        ItemConfigSwitch(
            icono = Icons.Default.Notifications,
            titulo = "Recordatorios de cuidado",
            descripcion = "Tu mascota te recordará cuidarla",
            checked = recordatoriosCuidado,
            onCheckedChange = onRecordatoriosChange,
            colorSwitch = colorPrincipal
        )

        Spacer(modifier = Modifier.height(8.dp))

        ItemConfigSwitch(
            icono = Icons.Default.Star,
            titulo = "Sonidos",
            descripcion = "Efectos de sonido al interactuar",
            checked = sonidos,
            onCheckedChange = onSonidosChange,
            colorSwitch = colorPrincipal
        )

        Spacer(modifier = Modifier.height(8.dp))

        ItemConfigSwitch(
            icono = Icons.Default.Star,
            titulo = "Animaciones",
            descripcion = "Movimientos de tu mascota",
            checked = animaciones,
            onCheckedChange = onAnimacionesChange,
            colorSwitch = colorPrincipal
        )
    }
}

/**
 * Header de sección
 */
@Composable
private fun SeccionHeaderConfig(titulo: String, icono: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF0F0F0),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF9C27B0),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = titulo,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF555555)
        )
    }
}

/**
 * Item de configuración con Switch
 */
@Composable
private fun ItemConfigSwitch(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colorSwitch: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorSwitch,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = descripcion,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.scale(0.9f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = colorSwitch,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
    }
}

/**
 * Item de configuración clickeable
 */
@Composable
private fun ItemConfigClickeable(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = descripcion,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

/**
 * Dialog para seleccionar moneda
 */
@Composable
private fun DialogSeleccionMoneda(
    monedaActual: String,
    onDismiss: () -> Unit,
    onSeleccionar: (String) -> Unit,
    color: Color
) {
    val monedas = listOf(
        "PEN - Sol Peruano",
        "USD - Dólar Estadounidense",
        "EUR - Euro",
        "GBP - Libra Esterlina",
        "JPY - Yen Japonés",
        "MXN - Peso Mexicano",
        "ARS - Peso Argentino",
        "CLP - Peso Chileno"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Seleccionar Moneda",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Spacer(modifier = Modifier.height(20.dp))

                monedas.forEach { moneda ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSeleccionar(moneda) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (moneda == monedaActual)
                                color.copy(alpha = 0.2f)
                            else
                                Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = moneda,
                                fontSize = 14.sp,
                                fontWeight = if (moneda == monedaActual) FontWeight.Bold else FontWeight.Normal
                            )
                            if (moneda == monedaActual) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog de Ayuda
 */
@Composable
private fun DialogAyuda(onDismiss: () -> Unit, color: Color) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ayuda")
            }
        },
        text = {
            Column {
                Text("📧 Correo: soporte@pocketpet.com")
                Spacer(modifier = Modifier.height(8.dp))
                Text("🌐 Web: www.pocketpet.com/ayuda")
                Spacer(modifier = Modifier.height(8.dp))
                Text("💬 Chat en vivo disponible 24/7")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = color)
            }
        }
    )
}

/**
 * Dialog Acerca de
 */
@Composable
private fun DialogAcerca(onDismiss: () -> Unit, color: Color) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🐕")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Acerca de PocketPet")
            }
        },
        text = {
            Column {
                Text(
                    text = "PocketPet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Versión 1.0.0")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tu compañero financiero que crece contigo. Gestiona tu dinero mientras cuidas de tu mascota virtual.")
                Spacer(modifier = Modifier.height(12.dp))
                Text("© 2024 PocketPet Team")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = color)
            }
        }
    )
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConfiguracion() {
    Configuracion()
}