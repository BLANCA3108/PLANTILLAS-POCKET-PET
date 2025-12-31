// MiPerfil.kt - VERSIÓN COMPLETA CON CERRAR SESIÓN
package com.lvmh.pocketpet.pantallas

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfil(
    onBack: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Colores
    val rosadoSuave = Color(0xFFFFB6C1)
    val moradoSuave = Color(0xFFE6B0FF)
    val rosadoClaro = Color(0xFFFFF0F5)
    val moradoClaro = Color(0xFFF8F0FF)
    val azulMasculino = Color(0xFF64B5F6)
    val rosadoFemenino = Color(0xFFF48FB1)
    val rojoError = Color(0xFFF44336)

    // Estados locales
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var generoSeleccionado by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }
    var mostrarDialogoUsuario by remember { mutableStateOf(false) }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados del ViewModel
    val isLoggingOut by authViewModel.isLoggingOut.collectAsState()

    // Animación
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
                    colors = listOf(rosadoClaro, moradoClaro)
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
                                    .shadow(elevation = 8.dp, shape = CircleShape)
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
                                "👤 Mi Perfil",
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Avatar animado
                AvatarAnimado(moradoSuave, generoSeleccionado)

                Spacer(modifier = Modifier.height(24.dp))

                // Nombre y usuario mostrados
                Text(
                    text = nombre.ifEmpty { "Sin nombre" },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = moradoSuave
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "@${usuario.ifEmpty { "usuario" }}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Sección de información personal
                SeccionInformacionPersonal(
                    nombre = nombre,
                    usuario = usuario,
                    generoSeleccionado = generoSeleccionado,
                    onEditarNombre = { mostrarDialogoNombre = true },
                    onEditarUsuario = { mostrarDialogoUsuario = true },
                    onSeleccionarGenero = { generoSeleccionado = it },
                    rosadoSuave = rosadoSuave,
                    moradoSuave = moradoSuave,
                    azulMasculino = azulMasculino,
                    rosadoFemenino = rosadoFemenino
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 🔥 BOTÓN DE CERRAR SESIÓN
                Button(
                    onClick = { mostrarDialogoCerrarSesion = true },
                    enabled = !isLoggingOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rojoError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cerrando sesión...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Mostrar error si existe
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = rojoError.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = rojoError,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = rojoError,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // 🔥 DIÁLOGO DE CONFIRMACIÓN DE CERRAR SESIÓN
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = rojoError,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¿Cerrar Sesión?",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas cerrar sesión? Tendrás que volver a iniciar sesión para acceder a tu cuenta.",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        authViewModel.signOut(
                            onSuccess = {
                                onLogoutSuccess()
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rojoError
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sí, cerrar sesión", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Diálogo para editar nombre
    if (mostrarDialogoNombre) {
        DialogoEdicion(
            titulo = "Editar Nombre",
            valor = nombre,
            onDismiss = { mostrarDialogoNombre = false },
            onConfirm = { nuevoNombre ->
                nombre = nuevoNombre
                mostrarDialogoNombre = false
            },
            placeholder = "Ingresa tu nombre completo",
            color = moradoSuave
        )
    }

    // Diálogo para editar usuario
    if (mostrarDialogoUsuario) {
        DialogoEdicion(
            titulo = "Editar Usuario",
            valor = usuario,
            onDismiss = { mostrarDialogoUsuario = false },
            onConfirm = { nuevoUsuario ->
                usuario = nuevoUsuario
                mostrarDialogoUsuario = false
            },
            placeholder = "Ingresa tu nombre de usuario",
            color = moradoSuave
        )
    }
}

/**
 * Avatar animado con género
 */
@Composable
private fun AvatarAnimado(color: Color, genero: String?) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val emoji = when (genero) {
        "Masculino" -> "👨"
        "Femenino" -> "👩"
        else -> "👤"
    }

    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale)
            .rotate(rotation)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = color.copy(alpha = 0.5f)
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFF8F0FF))
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 80.sp)
    }
}

/**
 * Sección de información personal
 */
@Composable
private fun SeccionInformacionPersonal(
    nombre: String,
    usuario: String,
    generoSeleccionado: String?,
    onEditarNombre: () -> Unit,
    onEditarUsuario: () -> Unit,
    onSeleccionarGenero: (String) -> Unit,
    rosadoSuave: Color,
    moradoSuave: Color,
    azulMasculino: Color,
    rosadoFemenino: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = moradoSuave,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF555555)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de nombre
            CampoEditable(
                label = "Nombre Completo",
                valor = nombre.ifEmpty { "No configurado" },
                icono = "👤",
                onClick = onEditarNombre,
                color = moradoSuave
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de usuario
            CampoEditable(
                label = "Nombre de Usuario",
                valor = usuario.ifEmpty { "No configurado" },
                icono = "✨",
                onClick = onEditarUsuario,
                color = rosadoSuave
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de género
            Text(
                text = "Género",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BurbujaGenero(
                    emoji = "👨",
                    label = "Masculino",
                    seleccionado = generoSeleccionado == "Masculino",
                    onClick = { onSeleccionarGenero("Masculino") },
                    color = azulMasculino,
                    modifier = Modifier.weight(1f)
                )

                BurbujaGenero(
                    emoji = "👩",
                    label = "Femenino",
                    seleccionado = generoSeleccionado == "Femenino",
                    onClick = { onSeleccionarGenero("Femenino") },
                    color = rosadoFemenino,
                    modifier = Modifier.weight(1f)
                )
            }

            if (generoSeleccionado != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Género seleccionado: $generoSeleccionado",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Campo editable
 */
@Composable
private fun CampoEditable(
    label: String,
    valor: String,
    icono: String,
    onClick: () -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icono, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (valor == "No configurado") Color.Gray else Color(0xFF333333)
            )
        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar",
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Burbuja de género
 */
@Composable
private fun BurbujaGenero(
    emoji: String,
    label: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (seleccionado) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(100.dp)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) color.copy(alpha = 0.2f) else Color(0xFFF5F5F5)
        ),
        border = if (seleccionado) {
            androidx.compose.foundation.BorderStroke(3.dp, color)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (seleccionado) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (seleccionado) color else Color.Gray
            )
        }
    }
}

/**
 * Diálogo de edición
 */
@Composable
private fun DialogoEdicion(
    titulo: String,
    valor: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    placeholder: String,
    color: Color
) {
    var textoEditado by remember { mutableStateOf(valor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                color = color
            )
        },
        text = {
            OutlinedTextField(
                value = textoEditado,
                onValueChange = { textoEditado = it },
                placeholder = { Text(placeholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = color,
                    focusedLabelColor = color,
                    cursorColor = color
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textoEditado) },
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMiPerfil() {
    MiPerfil()
}