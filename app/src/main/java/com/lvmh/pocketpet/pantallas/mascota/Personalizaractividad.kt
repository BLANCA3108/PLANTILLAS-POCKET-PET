package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class MascotaPersonalizada(
    val tipo: String = "🐶",
    val nombre: String = "POCKET PET",
    val colorFondo: Color = MoradoPrincipal,
    val accesorios: List<String> = emptyList(),
    val temaSeleccionado: String = "default"
)

data class ItemTienda(
    val id: String,
    val emoji: String,
    val nombre: String,
    val precio: Int,
    val tipo: TipoItem,
    val desbloqueado: Boolean = false,
    val equipado: Boolean = false
)

enum class TipoItem {
    MASCOTA,
    ACCESORIO,
    FONDO,
    TEMA
}

enum class CategoriaPersonalizacion {
    MASCOTAS,
    ACCESORIOS,
    FONDOS,
    TEMAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPersonalizarMascota() {
    var mascotaActual by remember { mutableStateOf(MascotaPersonalizada()) }
    var categoriaSeleccionada by remember { mutableStateOf(CategoriaPersonalizacion.MASCOTAS) }
    var monedasDisponibles by remember { mutableStateOf(850) }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }
    var mostrarDialogoCompra by remember { mutableStateOf<ItemTienda?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "respiracion")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Personalizar",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨", fontSize = 20.sp)
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
            VistaPrevia(
                mascota = mascotaActual,
                escala = escala,
                onEditarNombre = { mostrarDialogoNombre = true }
            )

            Spacer(modifier = Modifier.height(16.dp))
            CategoriasPersonalizacion(
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaSeleccionada = { categoriaSeleccionada = it }
            )
            Spacer(modifier = Modifier.height(12.dp))
            GridItems(
                categoria = categoriaSeleccionada,
                mascotaActual = mascotaActual,
                onItemClick = { item ->
                    if (item.desbloqueado) {
                        mascotaActual = aplicarItem(mascotaActual, item)
                    } else {
                        mostrarDialogoCompra = item
                    }
                }
            )
        }
    }

    if (mostrarDialogoNombre) {
        DialogoCambiarNombre(
            nombreActual = mascotaActual.nombre,
            onDismiss = { mostrarDialogoNombre = false },
            onConfirmar = { nuevoNombre ->
                mascotaActual = mascotaActual.copy(nombre = nuevoNombre)
                mostrarDialogoNombre = false
            }
        )
    }

    // Diálogo de compra
    mostrarDialogoCompra?.let { item ->
        DialogoCompra(
            item = item,
            monedasDisponibles = monedasDisponibles,
            onDismiss = { mostrarDialogoCompra = null },
            onComprar = {
                if (monedasDisponibles >= item.precio) {
                    monedasDisponibles -= item.precio
                    mascotaActual = aplicarItem(mascotaActual, item.copy(desbloqueado = true))
                }
                mostrarDialogoCompra = null
            }
        )
    }
}

@Composable
fun VistaPrevia(
    mascota: MascotaPersonalizada,
    escala: Float,
    onEditarNombre: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mascota.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoradoPrincipal
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onEditarNombre,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar nombre",
                        tint = GrisMedio,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(escala)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    mascota.colorFondo.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, mascota.colorFondo.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mascota.tipo,
                        fontSize = 80.sp
                    )
                }

                mascota.accesorios.forEachIndexed { index, accesorio ->
                    Text(
                        text = accesorio,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .align(
                                when (index % 4) {
                                    0 -> Alignment.TopEnd
                                    1 -> Alignment.TopStart
                                    2 -> Alignment.BottomEnd
                                    else -> Alignment.BottomStart
                                }
                            )
                            .offset(
                                x = if (index % 2 == 0) (-10).dp else 10.dp,
                                y = if (index < 2) 10.dp else (-10).dp
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Toca los items para equipar",
                fontSize = 12.sp,
                color = GrisMedio,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoriasPersonalizacion(
    categoriaSeleccionada: CategoriaPersonalizacion,
    onCategoriaSeleccionada: (CategoriaPersonalizacion) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(CategoriaPersonalizacion.entries) { categoria ->
            ChipCategoria(
                categoria = categoria,
                seleccionada = categoria == categoriaSeleccionada,
                onClick = { onCategoriaSeleccionada(categoria) }
            )
        }
    }
}

@Composable
fun ChipCategoria(
    categoria: CategoriaPersonalizacion,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val (emoji, texto, color) = when (categoria) {
        CategoriaPersonalizacion.MASCOTAS -> Triple("🐾", "Mascotas", RosaPastel)
        CategoriaPersonalizacion.ACCESORIOS -> Triple("👑", "Accesorios", AmarilloPastel)
        CategoriaPersonalizacion.FONDOS -> Triple("🎨", "Fondos", AzulPastel)
        CategoriaPersonalizacion.TEMAS -> Triple("✨", "Temas", MoradoClaro)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (seleccionada) color else Color.White,
        shadowElevation = if (seleccionada) 4.dp else 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = texto,
                fontSize = 14.sp,
                fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionada) Color.White else GrisTexto
            )
        }
    }
}

@Composable
fun GridItems(
    categoria: CategoriaPersonalizacion,
    mascotaActual: MascotaPersonalizada,
    onItemClick: (ItemTienda) -> Unit
) {
    val items = obtenerItemsPorCategoria(categoria)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            TarjetaItem(
                item = item,
                equipado = esItemEquipado(mascotaActual, item),
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun TarjetaItem(
    item: ItemTienda,
    equipado: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                equipado -> VerdeMentaClaro.copy(alpha = 0.3f)
                item.desbloqueado -> Color.White
                else -> GrisClaro.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(if (equipado) 6.dp else 3.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (equipado) BorderStroke(2.dp, VerdeMenta) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 40.sp,
                    modifier = Modifier.alpha(if (item.desbloqueado) 1f else 0.4f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.nombre,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (item.desbloqueado) GrisTexto else GrisMedio,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                if (!item.desbloqueado) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🪙", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.precio}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmarilloPastel
                        )
                    }
                }

                if (equipado) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Equipado",
                        tint = VerdeMenta,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!item.desbloqueado) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Bloqueado",
                    tint = GrisMedio,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DialogoCambiarNombre(
    nombreActual: String,
    onDismiss: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var nuevoNombre by remember { mutableStateOf(nombreActual) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(text = "✏️", fontSize = 32.sp) },
        title = {
            Text(
                text = "Cambiar Nombre",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Elige un nombre especial para tu mascota",
                    fontSize = 14.sp,
                    color = GrisMedio
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { if (it.length <= 15) nuevoNombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MoradoPrincipal,
                        focusedLabelColor = MoradoPrincipal
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${nuevoNombre.length}/15 caracteres",
                    fontSize = 11.sp,
                    color = GrisMedio
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (nuevoNombre.isNotBlank()) onConfirmar(nuevoNombre) },
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal),
                enabled = nuevoNombre.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GrisMedio)
            }
        }
    )
}

@Composable
fun DialogoCompra(
    item: ItemTienda,
    monedasDisponibles: Int,
    onDismiss: () -> Unit,
    onComprar: () -> Unit
) {
    val puedeComprar = monedasDisponibles >= item.precio

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(text = item.emoji, fontSize = 48.sp) },
        title = {
            Text(
                text = item.nombre,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🪙", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${item.precio}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmarilloPastel
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Monedas disponibles: $monedasDisponibles",
                    fontSize = 13.sp,
                    color = if (puedeComprar) VerdeMenta else CoralPastel,
                    fontWeight = FontWeight.Medium
                )

                if (!puedeComprar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Sigue ahorrando para desbloquear!",
                        fontSize = 12.sp,
                        color = GrisMedio,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onComprar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (puedeComprar) VerdeMenta else GrisClaro
                ),
                enabled = puedeComprar
            ) {
                Text(
                    if (puedeComprar) "Comprar" else "Sin monedas",
                    color = if (puedeComprar) Color.White else GrisMedio
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GrisMedio)
            }
        }
    )
}

fun aplicarItem(mascota: MascotaPersonalizada, item: ItemTienda): MascotaPersonalizada {
    return when (item.tipo) {
        TipoItem.MASCOTA -> mascota.copy(tipo = item.emoji)
        TipoItem.ACCESORIO -> {
            val nuevosAccesorios = if (mascota.accesorios.contains(item.emoji)) {
                mascota.accesorios - item.emoji
            } else {
                if (mascota.accesorios.size < 4) {
                    mascota.accesorios + item.emoji
                } else {
                    mascota.accesorios
                }
            }
            mascota.copy(accesorios = nuevosAccesorios)
        }
        TipoItem.FONDO -> mascota.copy(colorFondo = obtenerColorPorId(item.id))
        TipoItem.TEMA -> mascota.copy(temaSeleccionado = item.id)
    }
}

fun esItemEquipado(mascota: MascotaPersonalizada, item: ItemTienda): Boolean {
    return when (item.tipo) {
        TipoItem.MASCOTA -> mascota.tipo == item.emoji
        TipoItem.ACCESORIO -> mascota.accesorios.contains(item.emoji)
        TipoItem.FONDO -> mascota.colorFondo == obtenerColorPorId(item.id)
        TipoItem.TEMA -> mascota.temaSeleccionado == item.id
    }
}

fun obtenerColorPorId(id: String): Color {
    return when (id) {
        "fondo_morado" -> MoradoPrincipal
        "fondo_rosa" -> RosaPastel
        "fondo_verde" -> VerdeMenta
        "fondo_azul" -> AzulPastel
        "fondo_coral" -> CoralPastel
        "fondo_amarillo" -> AmarilloPastel
        else -> MoradoPrincipal
    }
}

fun obtenerItemsPorCategoria(categoria: CategoriaPersonalizacion): List<ItemTienda> {
    return when (categoria) {
        CategoriaPersonalizacion.MASCOTAS -> listOf(
            ItemTienda("perro", "🐶", "Perro", 0, TipoItem.MASCOTA, true, false),
            ItemTienda("gato", "🐱", "Gato", 100, TipoItem.MASCOTA, true, false),
            ItemTienda("conejo", "🐰", "Conejo", 150, TipoItem.MASCOTA, false, false),
            ItemTienda("zorro", "🦊", "Zorro", 200, TipoItem.MASCOTA, false, false),
            ItemTienda("oso", "🐻", "Oso", 250, TipoItem.MASCOTA, false, false),
            ItemTienda("panda", "🐼", "Panda", 300, TipoItem.MASCOTA, false, false),
            ItemTienda("koala", "🐨", "Koala", 350, TipoItem.MASCOTA, false, false),
            ItemTienda("unicornio", "🦄", "Unicornio", 500, TipoItem.MASCOTA, false, false),
            ItemTienda("dragon", "🐉", "Dragón", 800, TipoItem.MASCOTA, false, false)
        )
        CategoriaPersonalizacion.ACCESORIOS -> listOf(
            ItemTienda("corona", "👑", "Corona", 150, TipoItem.ACCESORIO, true, false),
            ItemTienda("gafas", "🕶️", "Gafas", 100, TipoItem.ACCESORIO, true, false),
            ItemTienda("sombrero", "🎩", "Sombrero", 120, TipoItem.ACCESORIO, false, false),
            ItemTienda("pajarita", "🎀", "Pajarita", 80, TipoItem.ACCESORIO, false, false),
            ItemTienda("estrella", "⭐", "Estrella", 200, TipoItem.ACCESORIO, false, false),
            ItemTienda("corazon", "💝", "Corazón", 180, TipoItem.ACCESORIO, false, false),
            ItemTienda("brillo", "✨", "Brillo", 150, TipoItem.ACCESORIO, false, false),
            ItemTienda("flor", "🌸", "Flor", 90, TipoItem.ACCESORIO, false, false),
            ItemTienda("fuego", "🔥", "Fuego", 300, TipoItem.ACCESORIO, false, false)
        )
        CategoriaPersonalizacion.FONDOS -> listOf(
            ItemTienda("fondo_morado", "🟣", "Morado", 0, TipoItem.FONDO, true, false),
            ItemTienda("fondo_rosa", "🩷", "Rosa", 100, TipoItem.FONDO, true, false),
            ItemTienda("fondo_verde", "💚", "Verde", 100, TipoItem.FONDO, false, false),
            ItemTienda("fondo_azul", "💙", "Azul", 100, TipoItem.FONDO, false, false),
            ItemTienda("fondo_coral", "🧡", "Coral", 150, TipoItem.FONDO, false, false),
            ItemTienda("fondo_amarillo", "💛", "Amarillo", 150, TipoItem.FONDO, false, false)
        )
        CategoriaPersonalizacion.TEMAS -> listOf(
            ItemTienda("tema_default", "✨", "Clásico", 0, TipoItem.TEMA, true, false),
            ItemTienda("tema_espacial", "🚀", "Espacial", 300, TipoItem.TEMA, false, false),
            ItemTienda("tema_oceano", "🌊", "Océano", 300, TipoItem.TEMA, false, false),
            ItemTienda("tema_bosque", "🌲", "Bosque", 300, TipoItem.TEMA, false, false),
            ItemTienda("tema_noche", "🌙", "Noche", 400, TipoItem.TEMA, false, false),
            ItemTienda("tema_arcoiris", "🌈", "Arcoíris", 500, TipoItem.TEMA, false, false)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPersonalizar() {
    MascotaFinancieraTheme {
        PantallaPersonalizarMascota()
    }
}