package com.lvmh.pocketpet.pantallas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class Transaccion(
    val id: Int,
    val concepto: String,
    val monto: Double,
    val fecha: String,
    val tipo: TipoTransaccion,
    val categoria: CategoriaTransaccion
)

enum class TipoTransaccion {
    INGRESO, GASTO
}

enum class CategoriaTransaccion(val icono: ImageVector, val nombre: String) {
    SALARIO(Icons.Default.Work, "Salario"),
    VENTA(Icons.Default.ShoppingBag, "Venta"),
    FREELANCE(Icons.Default.Computer, "Freelance"),
    SUPERMERCADO(Icons.Default.ShoppingCart, "Supermercado"),
    TRANSPORTE(Icons.Default.DirectionsCar, "Transporte"),
    RESTAURANTE(Icons.Default.Restaurant, "Restaurante"),
    ENTRETENIMIENTO(Icons.Default.Movie, "Entretenimiento"),
    SERVICIOS(Icons.Default.Receipt, "Servicios"),
    SALUD(Icons.Default.LocalHospital, "Salud"),
    OTROS(Icons.Default.Category, "Otros")
}

class DatosFinancieros {
    var transacciones = mutableStateListOf<Transaccion>()

    val saldoTotal: Double
        get() = transacciones.sumOf {
            if (it.tipo == TipoTransaccion.INGRESO) it.monto else -it.monto
        }

    val totalIngresos: Double
        get() = transacciones.filter { it.tipo == TipoTransaccion.INGRESO }.sumOf { it.monto }

    val totalGastos: Double
        get() = transacciones.filter { it.tipo == TipoTransaccion.GASTO }.sumOf { it.monto }

    fun agregarTransaccion(transaccion: Transaccion) {
        transacciones.add(0, transaccion)
    }

    fun obtenerSiguienteId(): Int {
        return if (transacciones.isEmpty()) 1 else transacciones.maxOf { it.id } + 1
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal() {
    val rosaPrimario = Color(0xFFE91E63)
    val moradoCalido = Color(0xFF9C27B0)
    val fondoApp = Color(0xFFFCE4EC)
    val verdeIngreso = Color(0xFF66BB6A)
    val rojoGasto = Color(0xFFEF5350)

    val datos = remember { DatosFinancieros() }
    var mostrarNotificaciones by remember { mutableStateOf(false) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var mostrarHistorial by remember { mutableStateOf(false) }

    val saldoAnimado by animateFloatAsState(
        targetValue = datos.saldoTotal.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "saldo"
    )

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
        maximumFractionDigits = 2
        currency = Currency.getInstance("PEN")
    }

    if (mostrarDialogoAgregar) {
        DialogoAgregarTransaccion(
            onDismiss = { mostrarDialogoAgregar = false },
            onAgregar = { transaccion ->
                datos.agregarTransaccion(transaccion)
                mostrarDialogoAgregar = false
            },
            siguienteId = datos.obtenerSiguienteId()
        )
    }

    if (mostrarHistorial) {
        DialogoHistorial(
            transacciones = datos.transacciones,
            onDismiss = { mostrarHistorial = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Pets,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "PocketPet",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row {
                            IconButton(onClick = { mostrarNotificaciones = !mostrarNotificaciones }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = Color.White
                                )
                            }

                            DropdownMenu(
                                expanded = mostrarNotificaciones,
                                onDismissRequest = { mostrarNotificaciones = false }
                            ) {
                                Text(
                                    "No hay notificaciones",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Configuración",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = moradoCalido
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoApp)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(rosaPrimario, moradoCalido)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SALDO DISPONIBLE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formatoMoneda.format(saldoAnimado.toDouble()),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Actualizado hoy",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = verdeIngreso),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ingresos",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "+${formatoMoneda.format(datos.totalIngresos)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = rojoGasto),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gastos",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Icon(
                                    Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "-${formatoMoneda.format(datos.totalGastos)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { mostrarDialogoAgregar = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = rosaPrimario),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Agregar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = moradoCalido
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Metas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { mostrarHistorial = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = moradoCalido
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Ver Historial Completo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            if (datos.transacciones.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = moradoCalido
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Últimas Transacciones",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = moradoCalido
                        )
                    }
                }

                items(datos.transacciones.take(5)) { transaccion ->
                    ItemTransaccion(transaccion)
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay transacciones aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Comienza agregando tu primera transacción",
                                fontSize = 14.sp,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ItemTransaccion(transaccion: Transaccion) {
    val verdeIngreso = Color(0xFF66BB6A)
    val rojoGasto = Color(0xFFEF5350)
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
        maximumFractionDigits = 2
        currency = Currency.getInstance("PEN")
    }

    var expanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.02f else 1f,
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val colorFondo = if (transaccion.tipo == TipoTransaccion.INGRESO)
                        verdeIngreso else rojoGasto

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(colorFondo.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            transaccion.categoria.icono,
                            contentDescription = null,
                            tint = colorFondo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = transaccion.concepto,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = transaccion.fecha,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    text = "${if (transaccion.tipo == TipoTransaccion.INGRESO) "+" else "-"}${formatoMoneda.format(transaccion.monto)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaccion.tipo == TipoTransaccion.INGRESO)
                        verdeIngreso else rojoGasto
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Categoría: ${transaccion.categoria.nombre}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "ID: ${transaccion.id}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            if (transaccion.tipo == TipoTransaccion.INGRESO)
                                "Ingreso" else "Gasto",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (transaccion.tipo == TipoTransaccion.INGRESO)
                                verdeIngreso else rojoGasto
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoAgregarTransaccion(
    onDismiss: () -> Unit,
    onAgregar: (Transaccion) -> Unit,
    siguienteId: Int
) {
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoTransaccion.GASTO) }
    var categoriaSeleccionada by remember { mutableStateOf(CategoriaTransaccion.OTROS) }
    var mostrarMenuCategorias by remember { mutableStateOf(false) }

    val rosaPrimario = Color(0xFFE91E63)
    val moradoCalido = Color(0xFF9C27B0)
    val verdeIngreso = Color(0xFF66BB6A)
    val rojoGasto = Color(0xFFEF5350)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Nueva Transacción",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = moradoCalido
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = tipoSeleccionado == TipoTransaccion.INGRESO,
                        onClick = { tipoSeleccionado = TipoTransaccion.INGRESO },
                        label = { Text("Ingreso") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = verdeIngreso,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = tipoSeleccionado == TipoTransaccion.GASTO,
                        onClick = { tipoSeleccionado = TipoTransaccion.GASTO },
                        label = { Text("Gasto") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = rojoGasto,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = concepto,
                    onValueChange = { concepto = it },
                    label = { Text("Concepto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = moradoCalido,
                        focusedLabelColor = moradoCalido
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = monto,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) monto = it },
                    label = { Text("Monto (S/)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    leadingIcon = {
                        Text("S/", fontWeight = FontWeight.Bold)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = moradoCalido,
                        focusedLabelColor = moradoCalido
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { mostrarMenuCategorias = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = moradoCalido
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    categoriaSeleccionada.icono,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(categoriaSeleccionada.nombre)
                            }
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = mostrarMenuCategorias,
                        onDismissRequest = { mostrarMenuCategorias = false }
                    ) {
                        CategoriaTransaccion.entries.forEach { categoria ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            categoria.icono,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(categoria.nombre)
                                    }
                                },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    mostrarMenuCategorias = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (concepto.isNotBlank() && monto.isNotBlank()) {
                                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale("es", "PE")).format(Date())
                                val nuevaTransaccion = Transaccion(
                                    id = siguienteId,
                                    concepto = concepto,
                                    monto = monto.toDoubleOrNull() ?: 0.0,
                                    fecha = fechaActual,
                                    tipo = tipoSeleccionado,
                                    categoria = categoriaSeleccionada
                                )
                                onAgregar(nuevaTransaccion)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = rosaPrimario),
                        enabled = concepto.isNotBlank() && monto.isNotBlank()
                    ) {
                        Text("Agregar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoHistorial(
    transacciones: List<Transaccion>,
    onDismiss: () -> Unit
) {
    val moradoCalido = Color(0xFF9C27B0)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(moradoCalido)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Historial Completo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }

                if (transacciones.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay transacciones",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transacciones) { transaccion ->
                            ItemTransaccion(transaccion)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaPrincipal() {
    MaterialTheme {
        PantallaPrincipal()
    }
}