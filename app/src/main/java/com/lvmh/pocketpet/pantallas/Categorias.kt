package com.lvmh.pocketpet.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Categorias(onBack: () -> Unit = {}) {
    // Colores cálidos rosado y morado suave
    val rosadoSuave = Color(0xFFFFB6C1)
    val moradoSuave = Color(0xFFE6B0FF)
    val rosadoClaro = Color(0xFFFFF0F5)
    val moradoClaro = Color(0xFFF8F0FF)

    // Estados
    var showDialog by remember { mutableStateOf(false) }
    var categoriasIngresos by remember {
        mutableStateOf(listOf(
            CategoriaData("Salario", "💰", true),
            CategoriaData("Bonos", "🎁", true),
            CategoriaData("Inversiones", "📈", true)
        ))
    }
    var categoriasGastos by remember {
        mutableStateOf(listOf(
            CategoriaData("Supermercado", "🛒", true),
            CategoriaData("Hogar", "🏠", true),
            CategoriaData("Entretenimiento", "🎮", true),
            CategoriaData("Transporte", "🚗", true),
            CategoriaData("Comida", "🍔", true)
        ))
    }

    // Animación de entrada
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
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
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
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
                                "🏷️ Categorías",
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
                MascotaCategoria(alpha)

                Spacer(modifier = Modifier.height(24.dp))

                // Sección INGRESOS
                SeccionHeader(
                    titulo = "INGRESOS",
                    emoji = "💵",
                    color = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(12.dp))

                categoriasIngresos.forEachIndexed { index, categoria ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        CategoriaItemMejorada(
                            categoria = categoria,
                            onToggle = { isEnabled ->
                                categoriasIngresos = categoriasIngresos.toMutableList().apply {
                                    this[index] = categoria.copy(isEnabled = isEnabled)
                                }
                            },
                            onDelete = {
                                categoriasIngresos = categoriasIngresos.filterIndexed { i, _ -> i != index }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sección GASTOS
                SeccionHeader(
                    titulo = "GASTOS",
                    emoji = "💸",
                    color = Color(0xFFFF6B6B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                categoriasGastos.forEachIndexed { index, categoria ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        CategoriaItemMejorada(
                            categoria = categoria,
                            onToggle = { isEnabled ->
                                categoriasGastos = categoriasGastos.toMutableList().apply {
                                    this[index] = categoria.copy(isEnabled = isEnabled)
                                }
                            },
                            onDelete = {
                                categoriasGastos = categoriasGastos.filterIndexed { i, _ -> i != index }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para agregar categoría
                BotonAgregarCategoria(
                    onClick = { showDialog = true },
                    color = moradoSuave
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Dialog para agregar categoría
        if (showDialog) {
            DialogAgregarCategoria(
                onDismiss = { showDialog = false },
                onAgregar = { nombre, emoji, tipo ->
                    val nuevaCategoria = CategoriaData(nombre, emoji, true)
                    if (tipo == "Ingreso") {
                        categoriasIngresos = categoriasIngresos + nuevaCategoria
                    } else {
                        categoriasGastos = categoriasGastos + nuevaCategoria
                    }
                    showDialog = false
                },
                colorFondo = moradoSuave
            )
        }
    }
}

/**
 * Mascota animada en la parte superior
 */
@Composable
private fun MascotaCategoria(alpha: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascota")

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
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
                text = "🐕",
                fontSize = 60.sp,
                modifier = Modifier.offset(y = bounce.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Organiza tus",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "categorías",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Header de sección (Ingresos/Gastos)
 */
@Composable
private fun SeccionHeader(titulo: String, emoji: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color.copy(alpha = 0.15f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = titulo,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

/**
 * Item de categoría mejorado
 */
@Composable
private fun CategoriaItemMejorada(
    categoria: CategoriaData,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
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
                Text(
                    text = categoria.emoji,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = categoria.nombre,
                    fontSize = 16.sp,
                    color = if (categoria.isEnabled) Color.Black else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = categoria.isEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.scale(0.9f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFE6B0FF),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFFF6B6B)
                    )
                }
            }
        }
    }

    // Dialog de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar categoría?") },
            text = { Text("¿Estás seguro de eliminar '${categoria.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Botón para agregar categoría
 */
@Composable
private fun BotonAgregarCategoria(onClick: () -> Unit, color: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Agregar Nueva Categoría",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

/**
 * Dialog para agregar nueva categoría
 */
@Composable
private fun DialogAgregarCategoria(
    onDismiss: () -> Unit,
    onAgregar: (String, String, String) -> Unit,
    colorFondo: Color
) {
    var nombre by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("📁") }
    var tipo by remember { mutableStateOf("Gasto") }

    val emojisDisponibles = listOf(
        "🛒", "🏠", "🚗", "🍔", "🎮", "💰", "📈", "🎁",
        "✈️", "🏥", "📚", "👕", "⚡", "💊", "🎵", "📱"
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
                    text = "Nueva Categoría",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorFondo
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Campo nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorFondo,
                        focusedLabelColor = colorFondo
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de emoji
                Text(
                    text = "Selecciona un emoji:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    emojisDisponibles.take(8).forEach { emojiOption ->
                        TextButton(
                            onClick = { emoji = emojiOption },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (emoji == emojiOption) colorFondo.copy(alpha = 0.2f)
                                    else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(text = emojiOption, fontSize = 24.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    emojisDisponibles.drop(8).forEach { emojiOption ->
                        TextButton(
                            onClick = { emoji = emojiOption },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (emoji == emojiOption) colorFondo.copy(alpha = 0.2f)
                                    else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(text = emojiOption, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de tipo
                Text(
                    text = "Tipo:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = tipo == "Ingreso",
                        onClick = { tipo = "Ingreso" },
                        label = { Text("💵 Ingreso") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = tipo == "Gasto",
                        onClick = { tipo = "Gasto" },
                        label = { Text("💸 Gasto") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (nombre.isNotBlank()) {
                                onAgregar(nombre, emoji, tipo)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorFondo
                        ),
                        enabled = nombre.isNotBlank()
                    ) {
                        Text("Agregar")
                    }
                }
            }
        }
    }
}

/**
 * Data class para categorías
 */
data class CategoriaData(
    val nombre: String,
    val emoji: String,
    val isEnabled: Boolean
)

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCategorias() {
    Categorias(onBack = {})
}