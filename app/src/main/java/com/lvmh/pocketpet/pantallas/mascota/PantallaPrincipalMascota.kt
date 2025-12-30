package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import kotlinx.coroutines.delay

/*----MASCOTAAAAAA---*/
data class EstadoMascota(
    val id: String = "",
    val nombre: String = "POCKET PET",
    val tipo: String = "🐶",
    val salud: Int = 75,
    val nivel: Int = 5,
    val experiencia: Int = 650,
    val experienciaMax: Int = 1000,
    val hambre: Int = 60,
    val felicidad: Int = 80,
    val energia: Int = 70,
    val monedasDisponibles: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalMascota(
    viewModel: MascotaViewModel,
    onNavegar: (String) -> Unit = {}
) {
    val estadoNullable by viewModel.estadoMascota.collectAsState()
    val primerIngreso by viewModel.primerIngreso.collectAsState()
    val debeMostrarPantallaPrincipal by viewModel.debeMostrarPantallaPrincipal.collectAsState()
    val monedasDisponibles by viewModel.monedasDisponibles.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeErrorState by viewModel.mensajeError.collectAsState()

    // Controlar la navegación automática
    LaunchedEffect(debeMostrarPantallaPrincipal) {
        if (debeMostrarPantallaPrincipal) {
            // No necesitamos navegar, solo mostrar la pantalla principal
            // El ViewModel ya actualizó el estado
        }
    }

    // Mostrar pantalla de selección si es primer ingreso
    if (primerIngreso || estadoNullable == null) {
        val mascotasDisponibles = listOf(
            MascotaData("Perrito", "🐶", "Amigable y leal"),
            MascotaData("Gatito", "🐱", "Independiente y curioso"),
            MascotaData("Conejito", "🐰", "Tierno y rápido"),
            MascotaData("Tortuga", "🐢", "Sabio y paciente"),
            MascotaData("Unicornio", "🦄", "Mágico y especial")
        )

        PantallaSeleccionMascota(
            mascotas = mascotasDisponibles,
            onMascotaSeleccionada = { mascota, nombre ->
                viewModel.seleccionarMascota(mascota.emoji, nombre)
            },
            onMascotaSorpresa = { nombre ->
                viewModel.mascotaSorpresa(nombre)
            }
        )

        // Mostrar loading si está cargando
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MoradoPrincipal)
            }
        }
        return
    }

    // Crear variable local no-null para evitar problemas de smart cast
    val estado = estadoNullable ?: return

    var pantalla_seleccionada by remember { mutableStateOf(1) }
    var menuDeslizableAbierto by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "respiracion")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala_respiracion"
    )

    var mensajeAccion by remember { mutableStateOf("") }

    // Mostrar mensaje de error si existe
    if (mensajeErrorState.isNotEmpty()) {
        LaunchedEffect(mensajeErrorState) {
            delay(3000)
            // Opción 1: Si tienes la función limpiarMensajeError()
            // viewModel.limpiarMensajeError()

            // Opción 2: Si mensajeError es MutableStateFlow público
            // viewModel.mensajeError.value = ""
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = CoralPastel,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralPastel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensajeErrorState,
                        fontSize = 14.sp,
                        color = GrisTexto,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Opción 1: Si tienes la función limpiarMensajeError()
                            // viewModel.limpiarMensajeError()

                            // Opción 2: Si mensajeError es MutableStateFlow público
                            // viewModel.mensajeError.value = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralPastel
                        )
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Mi Mascota",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🐾", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = "Monedas",
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "$monedasDisponibles",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { menuDeslizableAbierto = true }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BarraNavegacionInferior(
                pantalla_seleccionada = pantalla_seleccionada,
                onPantallaSeleccionada = { pantalla_seleccionada = it },
                onNavegar = onNavegar
            )
        }
    ) { paddingValues ->
        if (menuDeslizableAbierto) {
            MenuDeslizable(
                onClose = { menuDeslizableAbierto = false },
                onAccionSeleccionada = { accion ->
                    menuDeslizableAbierto = false
                    when (accion) {
                        "alimentar" -> onNavegar(RutasMascota.Cuidar.ruta)
                        "jugar" -> onNavegar(RutasMascota.MenuJuegos.ruta)
                        "cuidar" -> onNavegar(RutasMascota.Cuidar.ruta)
                        "tienda" -> onNavegar(RutasMascota.Personalizar.ruta)
                        "estadisticas" -> onNavegar(RutasMascota.Estadisticas.ruta)
                        "regalos" -> mensajeAccion = "¡Reclama tu regalo diario! 🎁"
                        "configuracion" -> mensajeAccion = "Configuración ⚙️"
                        "ayuda" -> onNavegar(RutasMascota.Mensajes.ruta)
                        else -> mensajeAccion = "Acción realizada"
                    }
                }
            )
        }

        if (mensajeAccion.isNotEmpty()) {
            LaunchedEffect(mensajeAccion) {
                delay(3000)
                mensajeAccion = ""
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // MOSTRAR NOMBRE DE LA MASCOTA CREADA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = estado.nombre,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoPrincipal
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MoradoPrincipal,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nivel ${estado.nivel}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                BarraExperiencia(
                    estado.experiencia,
                    estado.experienciaMax
                )
                Spacer(modifier = Modifier.height(32.dp))

                // MOSTRAR EL EMOJI REAL DE LA MASCOTA (no basado en salud)
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(escala),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        obtenerColorSalud(estado.salud).copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = estado.tipo, // Usar el emoji real de la mascota
                            fontSize = 100.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (estado.salud >= 80) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) {
                            Text(
                                text = "✨",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                TarjetaSalud(estado.salud)

                Spacer(modifier = Modifier.height(24.dp))

                FilaEstadisticasMascota(estado)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BotonAccion(
                        icono = Icons.Default.Restaurant,
                        texto = "Alimentar",
                        color = RosaPastel,
                        onClick = {
                            onNavegar(RutasMascota.Cuidar.ruta)
                        }
                    )
                    BotonAccion(
                        icono = Icons.Default.SportsEsports,
                        texto = "Jugar",
                        color = AmarilloPastel,
                        onClick = {
                            onNavegar(RutasMascota.MenuJuegos.ruta)
                        }
                    )
                    BotonAccion(
                        icono = Icons.Default.LocalHospital,
                        texto = "Curar",
                        color = VerdeMenta,
                        onClick = {
                            onNavegar(RutasMascota.Cuidar.ruta)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BotonAccion(
                        icono = Icons.Default.ShoppingCart,
                        texto = "Tienda",
                        color = AzulPastel,
                        onClick = {
                            onNavegar(RutasMascota.Personalizar.ruta)
                        }
                    )
                    BotonAccion(
                        icono = Icons.Default.Star,
                        texto = "Logros",
                        color = MoradoClaro,
                        onClick = {
                            onNavegar(RutasMascota.Logros.ruta)
                        }
                    )
                    BotonAccion(
                        icono = Icons.Default.Spa,
                        texto = "Evolución",
                        color = CoralPastel,
                        onClick = {
                            onNavegar(RutasMascota.Evolucion.ruta)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TarjetaEstadisticas()

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (mensajeAccion.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                ) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = MoradoPrincipal,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = mensajeAccion,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// FIN DE LA PARTE 1
// Confirma para continuar con la Parte 2

// FIN DE LA PARTE 1
// Confirma para continuar con la Parte 2

// FIN DE LA PARTE 1
// Confirma para continuar con la Parte 2
// PARTE 2 - Continúa después de la función PantallaPrincipalMascota

@Composable
fun MenuDeslizable(
    onClose: () -> Unit,
    onAccionSeleccionada: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClose() }
        )

        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .align(Alignment.TopEnd),
            shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp),
            shadowElevation = 16.dp,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Acciones",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoPrincipal
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                ItemMenu(
                    icono = Icons.Default.Restaurant,
                    texto = "Alimentar Mascota",
                    descripcion = "Mejora su salud y energía"
                ) { onAccionSeleccionada("alimentar") }

                ItemMenu(
                    icono = Icons.Default.SportsEsports,
                    texto = "Jugar Minijuegos",
                    descripcion = "Gana monedas y experiencia"
                ) { onAccionSeleccionada("jugar") }

                ItemMenu(
                    icono = Icons.Default.Spa,
                    texto = "Centro de Cuidado",
                    descripcion = "Recupera salud completa"
                ) { onAccionSeleccionada("cuidar") }

                ItemMenu(
                    icono = Icons.Default.ShoppingCart,
                    texto = "Tienda de Objetos",
                    descripcion = "Compra comida y accesorios"
                ) { onAccionSeleccionada("tienda") }

                ItemMenu(
                    icono = Icons.Default.Leaderboard,
                    texto = "Estadísticas Detalladas",
                    descripcion = "Ver progreso completo"
                ) { onAccionSeleccionada("estadisticas") }

                ItemMenu(
                    icono = Icons.Default.CardGiftcard,
                    texto = "Regalos Diarios",
                    descripcion = "Reclama tu premio"
                ) { onAccionSeleccionada("regalos") }

                ItemMenu(
                    icono = Icons.Default.Settings,
                    texto = "Configuración",
                    descripcion = "Ajustes de la aplicación"
                ) { onAccionSeleccionada("configuracion") }

                ItemMenu(
                    icono = Icons.Default.Help,
                    texto = "Ayuda y Soporte",
                    descripcion = "Cómo cuidar tu mascota"
                ) { onAccionSeleccionada("ayuda") }
            }
        }
    }
}

@Composable
fun ItemMenu(
    icono: ImageVector,
    texto: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = GrisClaro.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MoradoPrincipal,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = texto,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                    fontSize = 16.sp
                )
                Text(
                    text = descripcion,
                    color = GrisMedio,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun FilaEstadisticasMascota(estado: EstadoMascota) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EstadisticaMini(
            emoji = "🍖",
            valor = "${estado.hambre}%",
            etiqueta = "Hambre",
            color = if (estado.hambre < 30) CoralPastel else VerdeMenta
        )
        EstadisticaMini(
            emoji = "😊",
            valor = "${estado.felicidad}%",
            etiqueta = "Felicidad",
            color = if (estado.felicidad < 30) CoralPastel else AmarilloPastel
        )
        EstadisticaMini(
            emoji = "⚡",
            valor = "${estado.energia}%",
            etiqueta = "Energía",
            color = if (estado.energia < 30) CoralPastel else AzulPastel
        )
    }
}

@Composable
fun EstadisticaMini(
    emoji: String,
    valor: String,
    etiqueta: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )
        Text(
            text = etiqueta,
            fontSize = 10.sp,
            color = GrisMedio
        )
    }
}

@Composable
fun BotonAccion(
    icono: ImageVector,
    texto: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(85.dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White,
            modifier = Modifier.size(60.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                icono,
                contentDescription = texto,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            texto,
            fontSize = 12.sp,
            color = GrisTexto,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BarraNavegacionInferior(
    pantalla_seleccionada: Int,
    onPantallaSeleccionada: (Int) -> Unit,
    onNavegar: (String) -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Inicio", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 0,
            onClick = {
                onPantallaSeleccionada(0)
                onNavegar("inicio")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GrisTexto,
                selectedTextColor = GrisTexto,
                indicatorColor = GrisClaro,
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Mascota",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Mascota", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 1,
            onClick = {
                onPantallaSeleccionada(1)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RosaPastel,
                selectedTextColor = RosaPastel,
                indicatorColor = RosaPastelClaro.copy(alpha = 0.3f),
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Análisis",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Análisis", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 2,
            onClick = {
                onPantallaSeleccionada(2)
                onNavegar(RutasMascota.Estadisticas.ruta)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GrisTexto,
                selectedTextColor = GrisTexto,
                indicatorColor = GrisClaro,
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Más",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Más", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 3,
            onClick = {
                onPantallaSeleccionada(3)
                onNavegar("configuracion")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GrisTexto,
                selectedTextColor = GrisTexto,
                indicatorColor = GrisClaro,
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
    }
}

@Composable
fun BarraExperiencia(actual: Int, maximo: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✨", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Experiencia",
                    fontSize = 15.sp,
                    color = GrisMedio,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                "$actual / $maximo XP",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MoradoPrincipal
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(GrisClaro)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(actual.toFloat() / maximo.toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(MoradoPrincipal, MoradoClaro)
                        )
                    )
            )
        }
    }
}

@Composable
fun TarjetaSalud(salud: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💚", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Estado de Salud",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(130.dp),
                    strokeWidth = 12.dp,
                    color = GrisClaro,
                    trackColor = Color.Transparent
                )

                CircularProgressIndicator(
                    progress = { salud / 100f },
                    modifier = Modifier.size(130.dp),
                    strokeWidth = 12.dp,
                    color = obtenerColorSalud(salud),
                    trackColor = Color.Transparent
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$salud",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = obtenerColorSalud(salud)
                    )
                    Text(
                        obtenerTextoEstado(salud),
                        fontSize = 16.sp,
                        color = GrisMedio,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaEstadisticas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ItemEstadistica("💰", "S/ 1,250", "Ahorrado")
            HorizontalDivider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = GrisClaro
            )
            ItemEstadistica("📊", "23 días", "Racha")
            HorizontalDivider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = GrisClaro
            )
            ItemEstadistica("🎯", "3/5", "Metas")
        }
    }
}

@Composable
fun ItemEstadistica(emoji: String, valor: String, etiqueta: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(emoji, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            valor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )
        Text(
            etiqueta,
            fontSize = 12.sp,
            color = GrisMedio
        )
    }
}

fun obtenerColorSalud(salud: Int): Color {
    return when (salud) {
        in 0..20 -> CoralPastel
        in 21..40 -> AmarilloPastel
        in 41..60 -> AzulPastel
        in 61..80 -> VerdeMenta
        else -> MoradoPrincipal
    }
}

fun obtenerTextoEstado(salud: Int): String {
    return when (salud) {
        in 0..20 -> "Crítico"
        in 21..40 -> "Alerta"
        in 41..60 -> "Estable"
        in 61..80 -> "Saludable"
        else -> "Próspero"
    }
}

// FIN DEL ARCHIVO PantallaPrincipalMascota.kt