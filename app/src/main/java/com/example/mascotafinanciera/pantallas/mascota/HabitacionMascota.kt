package com.example.mascotafinanciera.pantallas.mascota

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*

data class Habitacion(
    val nivel: Int = 1,
    val estilo: EstiloHabitacion = EstiloHabitacion.BASICO,
    val muebles: List<Mueble> = emptyList(),
    val decoraciones: List<Decoracion> = emptyList(),
    val mascota: String = "🐶",
    val ambiente: String = "☀️"
)

enum class EstiloHabitacion(val emoji: String, val nombre: String, val color: Color) {
    BASICO("🏠", "Básico", GrisClaro),
    ACOGEDOR("🛋️", "Acogedor", RosaPastelClaro),
    MODERNO("🏢", "Moderno", AzulCielo),
    LUJO("💎", "Lujo", MoradoClaro),
    JARDIN("🌸", "Jardín", VerdeMentaClaro)
}

data class Mueble(
    val id: String,
    val emoji: String,
    val nombre: String,
    val tipo: TipoMueble,
    val precio: Int,
    val nivelRequerido: Int,
    val desbloqueado: Boolean = false,
    val colocado: Boolean = false,
    val posicion: PosicionMueble = PosicionMueble.CENTRO
)

enum class TipoMueble {
    CAMA,
    MESA,
    SILLA,
    PLANTA,
    LAMPARA,
    ESTANTE,
    ALFOMBRA,
    VENTANA
}

enum class PosicionMueble {
    IZQUIERDA,
    CENTRO,
    DERECHA,
    ARRIBA
}

data class Decoracion(
    val emoji: String,
    val nombre: String,
    val precio: Int,
    val colocada: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHabitacionMascota() {
    var habitacion by remember {
        mutableStateOf(
            Habitacion(
                nivel = 5,
                muebles = listOf(
                    Mueble("cama1", "🛏️", "Cama", TipoMueble.CAMA, 0, 1, true, true, PosicionMueble.IZQUIERDA),
                    Mueble("planta1", "🪴", "Planta", TipoMueble.PLANTA, 50, 1, true, true, PosicionMueble.DERECHA)
                )
            )
        )
    }
    var monedasDisponibles by remember { mutableStateOf(1200) }
    var categoriaSeleccionada by remember { mutableStateOf(TipoMueble.CAMA) }
    var mostrarTienda by remember { mutableStateOf(false) }
    var muebleSeleccionado by remember { mutableStateOf<Mueble?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "mascota_movimiento")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "salto"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Mi Habitación",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🏠", fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Volver */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AmarilloPastel,
                        shadowElevation = 2.dp,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$monedasDisponibles",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    IconButton(onClick = { mostrarTienda = !mostrarTienda }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Tienda",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp)
        ) {
            if (!mostrarTienda) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    InfoNivelHabitacion(habitacion.nivel, habitacion.estilo)

                    Spacer(modifier = Modifier.height(16.dp))

                    VistaHabitacion(
                        habitacion = habitacion,
                        offsetY = offsetY,
                        onMuebleClick = { mueble ->
                            muebleSeleccionado = mueble
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SeccionEstilos(
                        estiloActual = habitacion.estilo,
                        onEstiloSeleccionado = { nuevoEstilo ->
                            habitacion = habitacion.copy(estilo = nuevoEstilo)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    CategoriasMuebles(
                        categoriaSeleccionada = categoriaSeleccionada,
                        onCategoriaSeleccionada = { categoriaSeleccionada = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TiendaMuebles(
                        categoria = categoriaSeleccionada,
                        nivelActual = habitacion.nivel,
                        muebles = habitacion.muebles,
                        monedasDisponibles = monedasDisponibles,
                        onComprar = { mueble ->
                            if (monedasDisponibles >= mueble.precio) {
                                monedasDisponibles -= mueble.precio
                                habitacion = habitacion.copy(
                                    muebles = habitacion.muebles + mueble.copy(desbloqueado = true)
                                )
                            }
                        },
                        onColocar = { mueble ->
                            habitacion = habitacion.copy(
                                muebles = habitacion.muebles.map {
                                    if (it.id == mueble.id) it.copy(colocado = !it.colocado)
                                    else it
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    muebleSeleccionado?.let { mueble ->
        DialogoMueble(
            mueble = mueble,
            onDismiss = { muebleSeleccionado = null },
            onQuitar = {
                habitacion = habitacion.copy(
                    muebles = habitacion.muebles.map {
                        if (it.id == mueble.id) it.copy(colocado = false)
                        else it
                    }
                )
                muebleSeleccionado = null
            }
        )
    }
}

@Composable
fun InfoNivelHabitacion(nivel: Int, estilo: EstiloHabitacion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MoradoPrincipal.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏠", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Nivel $nivel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )
                    Text(
                        text = "Estilo: ${estilo.nombre}",
                        fontSize = 13.sp,
                        color = GrisMedio
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = estilo.color
            ) {
                Text(
                    text = estilo.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun VistaHabitacion(
    habitacion: Habitacion,
    offsetY: Float,
    onMuebleClick: (Mueble) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = habitacion.estilo.color.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            habitacion.estilo.color.copy(alpha = 0.2f),
                            habitacion.estilo.color.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = habitacion.ambiente,
                        fontSize = 40.sp,
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f)
                ) {
                    habitacion.muebles.filter { it.colocado }.forEach { mueble ->
                        val alignment = when (mueble.posicion) {
                            PosicionMueble.IZQUIERDA -> Alignment.BottomStart
                            PosicionMueble.CENTRO -> Alignment.BottomCenter
                            PosicionMueble.DERECHA -> Alignment.BottomEnd
                            PosicionMueble.ARRIBA -> Alignment.TopCenter
                        }

                        Text(
                            text = mueble.emoji,
                            fontSize = 60.sp,
                            modifier = Modifier
                                .align(alignment)
                                .padding(
                                    when (mueble.posicion) {
                                        PosicionMueble.IZQUIERDA -> PaddingValues(start = 20.dp, bottom = 30.dp)
                                        PosicionMueble.DERECHA -> PaddingValues(end = 20.dp, bottom = 30.dp)
                                        PosicionMueble.CENTRO -> PaddingValues(bottom = 30.dp)
                                        PosicionMueble.ARRIBA -> PaddingValues(top = 20.dp)
                                    }
                                )
                                .clickable { onMuebleClick(mueble) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = offsetY.dp)
                    ) {
                        Text(
                            text = habitacion.mascota,
                            fontSize = 80.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF8B7355).copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            if (habitacion.muebles.filter { it.colocado }.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🛒", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Decora tu habitación!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto
                        )
                        Text(
                            text = "Compra muebles en la tienda",
                            fontSize = 12.sp,
                            color = GrisMedio,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionEstilos(
    estiloActual: EstiloHabitacion,
    onEstiloSeleccionado: (EstiloHabitacion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Estilos de Habitación",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(EstiloHabitacion.entries) { estilo ->
                TarjetaEstilo(
                    estilo = estilo,
                    seleccionado = estilo == estiloActual,
                    onClick = { onEstiloSeleccionado(estilo) }
                )
            }
        }
    }
}

@Composable
fun TarjetaEstilo(
    estilo: EstiloHabitacion,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) estilo.color else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (seleccionado) 6.dp else 3.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (seleccionado) androidx.compose.foundation.BorderStroke(2.dp, MoradoPrincipal) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = estilo.emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = estilo.nombre,
                fontSize = 12.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionado) GrisTexto else GrisMedio,
                textAlign = TextAlign.Center
            )
            if (seleccionado) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = VerdeMenta,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CategoriasMuebles(
    categoriaSeleccionada: TipoMueble,
    onCategoriaSeleccionada: (TipoMueble) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TipoMueble.entries) { categoria ->
            val (emoji, nombre) = when (categoria) {
                TipoMueble.CAMA -> "🛏️" to "Camas"
                TipoMueble.MESA -> "🪑" to "Mesas"
                TipoMueble.SILLA -> "🪑" to "Sillas"
                TipoMueble.PLANTA -> "🪴" to "Plantas"
                TipoMueble.LAMPARA -> "💡" to "Lámparas"
                TipoMueble.ESTANTE -> "📚" to "Estantes"
                TipoMueble.ALFOMBRA -> "🟥" to "Alfombras"
                TipoMueble.VENTANA -> "🪟" to "Ventanas"
            }

            ChipCategoriaMueble(
                emoji = emoji,
                texto = nombre,
                seleccionado = categoria == categoriaSeleccionada,
                onClick = { onCategoriaSeleccionada(categoria) }
            )
        }
    }
}

@Composable
fun ChipCategoriaMueble(
    emoji: String,
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (seleccionado) MoradoPrincipal else Color.White,
        shadowElevation = if (seleccionado) 4.dp else 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionado) Color.White else GrisTexto
            )
        }
    }
}

@Composable
fun TiendaMuebles(
    categoria: TipoMueble,
    nivelActual: Int,
    muebles: List<Mueble>,
    monedasDisponibles: Int,
    onComprar: (Mueble) -> Unit,
    onColocar: (Mueble) -> Unit
) {
    val mueblesPorCategoria = obtenerMueblesPorCategoria(categoria)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mueblesPorCategoria) { mueble ->
            val muebleActual = muebles.find { it.id == mueble.id }
            val desbloqueado = muebleActual?.desbloqueado ?: false
            val colocado = muebleActual?.colocado ?: false
            val bloqueadoPorNivel = mueble.nivelRequerido > nivelActual

            TarjetaMueble(
                mueble = mueble,
                desbloqueado = desbloqueado,
                colocado = colocado,
                bloqueadoPorNivel = bloqueadoPorNivel,
                puedeComprar = monedasDisponibles >= mueble.precio,
                onComprar = { onComprar(mueble) },
                onColocar = { if (desbloqueado) onColocar(mueble) }
            )
        }
    }
}

@Composable
fun TarjetaMueble(
    mueble: Mueble,
    desbloqueado: Boolean,
    colocado: Boolean,
    bloqueadoPorNivel: Boolean,
    puedeComprar: Boolean,
    onComprar: () -> Unit,
    onColocar: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                colocado -> VerdeMentaClaro.copy(alpha = 0.3f)
                desbloqueado -> Color.White
                else -> GrisClaro.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(if (colocado) 6.dp else 3.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (colocado) androidx.compose.foundation.BorderStroke(2.dp, VerdeMenta) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = mueble.emoji,
                    fontSize = 48.sp,
                    modifier = Modifier.alpha(if (desbloqueado || !bloqueadoPorNivel) 1f else 0.4f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mueble.nombre,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (desbloqueado) GrisTexto else GrisMedio,
                    textAlign = TextAlign.Center
                )

                if (bloqueadoPorNivel) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CoralPastel.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = CoralPastel,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Nivel ${mueble.nivelRequerido}",
                                fontSize = 10.sp,
                                color = CoralPastel,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            when {
                colocado -> {
                    Button(
                        onClick = onColocar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralPastel
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Quitar", fontSize = 12.sp)
                    }
                }
                desbloqueado -> {
                    Button(
                        onClick = onColocar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeMenta
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Colocar", fontSize = 12.sp)
                    }
                }
                bloqueadoPorNivel -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GrisClaro
                    ) {
                        Text(
                            text = "Bloqueado",
                            fontSize = 12.sp,
                            color = GrisMedio,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    Button(
                        onClick = onComprar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (puedeComprar) AmarilloPastel else GrisClaro
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = puedeComprar,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${mueble.precio}",
                                fontSize = 12.sp,
                                color = if (puedeComprar) Color.White else GrisMedio
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoMueble(
    mueble: Mueble,
    onDismiss: () -> Unit,
    onQuitar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(text = mueble.emoji, fontSize = 48.sp) },
        title = {
            Text(
                text = mueble.nombre,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "¿Deseas quitar este mueble de la habitación?",
                fontSize = 14.sp,
                color = GrisMedio,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onQuitar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralPastel
                )
            ) {
                Text("Quitar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GrisMedio)
            }
        }
    )
}

fun obtenerMueblesPorCategoria(categoria: TipoMueble): List<Mueble> {
    return when (categoria) {
        TipoMueble.CAMA -> listOf(
            Mueble("cama1", "🛏️", "Cama Simple", TipoMueble.CAMA, 0, 1, false, false, PosicionMueble.IZQUIERDA),
            Mueble("cama2", "🛌", "Cama Doble", TipoMueble.CAMA, 200, 3, false, false, PosicionMueble.IZQUIERDA),
            Mueble("cama3", "🏨", "Cama King", TipoMueble.CAMA, 500, 5, false, false, PosicionMueble.IZQUIERDA),
            Mueble("cama4", "💎", "Cama de Lujo", TipoMueble.CAMA, 1000, 8, false, false, PosicionMueble.IZQUIERDA)
        )
        TipoMueble.MESA -> listOf(
            Mueble("mesa1", "🪑", "Mesa Básica", TipoMueble.MESA, 100, 1, false, false, PosicionMueble.CENTRO),
            Mueble("mesa2", "🍽️", "Mesa Comedor", TipoMueble.MESA, 250, 3, false, false, PosicionMueble.CENTRO),
            Mueble("mesa3", "🎮", "Mesa Gaming", TipoMueble.MESA, 400, 5, false, false, PosicionMueble.CENTRO),
            Mueble("mesa4", "💼", "Escritorio Pro", TipoMueble.MESA, 800, 7, false, false, PosicionMueble.CENTRO)
        )
        TipoMueble.SILLA -> listOf(
            Mueble("silla1", "🪑", "Silla Simple", TipoMueble.SILLA, 50, 1, false, false, PosicionMueble.CENTRO),
            Mueble("silla2", "💺", "Silla Cómoda", TipoMueble.SILLA, 150, 2, false, false, PosicionMueble.CENTRO),
            Mueble("silla3", "🎯", "Silla Gamer", TipoMueble.SILLA, 350, 4, false, false, PosicionMueble.CENTRO),
            Mueble("silla4", "👔", "Silla Ejecutiva", TipoMueble.SILLA, 600, 6, false, false, PosicionMueble.CENTRO)
        )
        TipoMueble.PLANTA -> listOf(
            Mueble("planta1", "🪴", "Planta Pequeña", TipoMueble.PLANTA, 50, 1, false, false, PosicionMueble.DERECHA),
            Mueble("planta2", "🌿", "Helecho", TipoMueble.PLANTA, 100, 2, false, false, PosicionMueble.DERECHA),
            Mueble("planta3", "🌵", "Cactus", TipoMueble.PLANTA, 150, 3, false, false, PosicionMueble.DERECHA),
            Mueble("planta4", "🌴", "Palmera", TipoMueble.PLANTA, 300, 4, false, false, PosicionMueble.DERECHA),
            Mueble("planta5", "🌺", "Flor Tropical", TipoMueble.PLANTA, 400, 5, false, false, PosicionMueble.DERECHA),
            Mueble("planta6", "🌸", "Cerezo", TipoMueble.PLANTA, 800, 7, false, false, PosicionMueble.DERECHA)
        )
        TipoMueble.LAMPARA -> listOf(
            Mueble("lampara1", "💡", "Bombilla", TipoMueble.LAMPARA, 80, 1, false, false, PosicionMueble.ARRIBA),
            Mueble("lampara2", "🕯️", "Vela", TipoMueble.LAMPARA, 120, 2, false, false, PosicionMueble.ARRIBA),
            Mueble("lampara3", "🔦", "Lámpara LED", TipoMueble.LAMPARA, 200, 3, false, false, PosicionMueble.ARRIBA),
            Mueble("lampara4", "💫", "Lámpara Estrella", TipoMueble.LAMPARA, 350, 5, false, false, PosicionMueble.ARRIBA),
            Mueble("lampara5", "🌟", "Araña de Luces", TipoMueble.LAMPARA, 700, 7, false, false, PosicionMueble.ARRIBA)
        )
        TipoMueble.ESTANTE -> listOf(
            Mueble("estante1", "📚", "Estante Básico", TipoMueble.ESTANTE, 150, 2, false, false, PosicionMueble.DERECHA),
            Mueble("estante2", "📖", "Librería", TipoMueble.ESTANTE, 300, 3, false, false, PosicionMueble.DERECHA),
            Mueble("estante3", "🎨", "Estante Moderno", TipoMueble.ESTANTE, 450, 5, false, false, PosicionMueble.DERECHA),
            Mueble("estante4", "🏆", "Vitrina Trofeos", TipoMueble.ESTANTE, 800, 7, false, false, PosicionMueble.DERECHA)
        )
        TipoMueble.ALFOMBRA -> listOf(
            Mueble("alfombra1", "🟥", "Alfombra Roja", TipoMueble.ALFOMBRA, 100, 1, false, false, PosicionMueble.CENTRO),
            Mueble("alfombra2", "🟦", "Alfombra Azul", TipoMueble.ALFOMBRA, 100, 1, false, false, PosicionMueble.CENTRO),
            Mueble("alfombra3", "🟩", "Alfombra Verde", TipoMueble.ALFOMBRA, 100, 1, false, false, PosicionMueble.CENTRO),
            Mueble("alfombra4", "🟨", "Alfombra Dorada", TipoMueble.ALFOMBRA, 250, 3, false, false, PosicionMueble.CENTRO),
            Mueble("alfombra5", "🎨", "Alfombra Persa", TipoMueble.ALFOMBRA, 500, 5, false, false, PosicionMueble.CENTRO),
            Mueble("alfombra6", "✨", "Alfombra Mágica", TipoMueble.ALFOMBRA, 1000, 8, false, false, PosicionMueble.CENTRO)
        )
        TipoMueble.VENTANA -> listOf(
            Mueble("ventana1", "🪟", "Ventana Simple", TipoMueble.VENTANA, 150, 2, false, false, PosicionMueble.ARRIBA),
            Mueble("ventana2", "🌅", "Ventana Grande", TipoMueble.VENTANA, 300, 4, false, false, PosicionMueble.ARRIBA),
            Mueble("ventana3", "🌃", "Ventana Ciudad", TipoMueble.VENTANA, 500, 6, false, false, PosicionMueble.ARRIBA),
            Mueble("ventana4", "🌌", "Ventana Espacial", TipoMueble.VENTANA, 900, 8, false, false, PosicionMueble.ARRIBA)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHabitacion() {
    MascotaFinancieraTheme {
        PantallaHabitacionMascota()
    }
}