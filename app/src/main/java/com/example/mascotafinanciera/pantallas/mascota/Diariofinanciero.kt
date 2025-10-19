package com.example.mascotafinanciera.pantallas.mascota

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class EntradaDiario(
    val id: Int,
    val fecha: Date,
    val tipo: TipoTransaccion,
    val monto: Double,
    val categoria: String,
    val descripcion: String,
    val impactoMascota: ImpactoMascota,
    val mensajeMascota: String
)

enum class TipoTransaccion {
    INGRESO,
    GASTO,
    AHORRO,
    META
}

data class ImpactoMascota(
    val saludAntes: Int,
    val saludDespues: Int,
    val experienciaGanada: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDiarioFinanciero() {
    var filtroSeleccionado by remember { mutableStateOf<TipoTransaccion?>(null) }
    var mesSeleccionado by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }

    val entradas = remember { obtenerEntradasEjemplo() }

    val entradasFiltradas = entradas.filter { entrada ->
        val calendario = Calendar.getInstance()
        calendario.time = entrada.fecha
        val coincideMes = calendario.get(Calendar.MONTH) == mesSeleccionado
        val coincideTipo = filtroSeleccionado == null || entrada.tipo == filtroSeleccionado
        coincideMes && coincideTipo
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Diario Financiero",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📖", fontSize = 20.sp)
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
                    IconButton(onClick = { /* Filtros avanzados */ }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtros",
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
            // Filtros rápidos
            FiltrosRapidos(
                filtroSeleccionado = filtroSeleccionado,
                onFiltroClick = {
                    filtroSeleccionado = if (filtroSeleccionado == it) null else it
                }
            )

            // Selector de mes
            SelectorMes(
                mesActual = mesSeleccionado,
                onMesSeleccionado = { mesSeleccionado = it }
            )

            // Resumen del mes
            ResumenMensual(entradas = entradasFiltradas)

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de entradas
            if (entradasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🐾",
                            fontSize = 60.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sin movimientos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisMedio
                        )
                        Text(
                            text = "No hay registros en este período",
                            fontSize = 14.sp,
                            color = GrisMedio
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(entradasFiltradas.sortedByDescending { it.fecha }) { entrada ->
                        TarjetaEntradaDiario(entrada = entrada)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FiltrosRapidos(
    filtroSeleccionado: TipoTransaccion?,
    onFiltroClick: (TipoTransaccion) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChipFiltro(
            texto = "Ingresos",
            icono = "💰",
            seleccionado = filtroSeleccionado == TipoTransaccion.INGRESO,
            color = VerdeMenta,
            onClick = { onFiltroClick(TipoTransaccion.INGRESO) }
        )
        ChipFiltro(
            texto = "Gastos",
            icono = "💸",
            seleccionado = filtroSeleccionado == TipoTransaccion.GASTO,
            color = CoralPastel,
            onClick = { onFiltroClick(TipoTransaccion.GASTO) }
        )
        ChipFiltro(
            texto = "Ahorro",
            icono = "🐷",
            seleccionado = filtroSeleccionado == TipoTransaccion.AHORRO,
            color = AzulPastel,
            onClick = { onFiltroClick(TipoTransaccion.AHORRO) }
        )
    }
}

@Composable
fun RowScope.ChipFiltro(
    texto: String,
    icono: String,
    seleccionado: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        color = if (seleccionado) color else Color.White,
        shadowElevation = if (seleccionado) 4.dp else 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icono, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionado) Color.White else GrisTexto,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SelectorMes(
    mesActual: Int,
    onMesSeleccionado: (Int) -> Unit
) {
    val nombresMeses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (mesActual > 0) onMesSeleccionado(mesActual - 1)
            },
            enabled = mesActual > 0
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Mes anterior",
                tint = if (mesActual > 0) MoradoPrincipal else GrisClaro
            )
        }

        Text(
            text = nombresMeses[mesActual],
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MoradoPrincipal
        )

        IconButton(
            onClick = {
                if (mesActual < 11) onMesSeleccionado(mesActual + 1)
            },
            enabled = mesActual < 11
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Mes siguiente",
                tint = if (mesActual < 11) MoradoPrincipal else GrisClaro
            )
        }
    }
}

@Composable
fun ResumenMensual(entradas: List<EntradaDiario>) {
    val totalIngresos = entradas.filter { it.tipo == TipoTransaccion.INGRESO }
        .sumOf { it.monto }
    val totalGastos = entradas.filter { it.tipo == TipoTransaccion.GASTO }
        .sumOf { it.monto }
    val balance = totalIngresos - totalGastos

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ItemResumen("💰", String.format("S/ %.2f", totalIngresos), "Ingresos", VerdeMenta)
            Divider(modifier = Modifier.height(50.dp).width(1.dp), color = GrisClaro)
            ItemResumen("💸", String.format("S/ %.2f", totalGastos), "Gastos", CoralPastel)
            Divider(modifier = Modifier.height(50.dp).width(1.dp), color = GrisClaro)
            ItemResumen(
                if (balance >= 0) "📈" else "📉",
                String.format("S/ %.2f", balance),
                "Balance",
                if (balance >= 0) VerdeMenta else CoralPastel
            )
        }
    }
}

@Composable
fun ItemResumen(emoji: String, valor: String, etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = etiqueta,
            fontSize = 11.sp,
            color = GrisMedio
        )
    }
}

@Composable
fun TarjetaEntradaDiario(entrada: EntradaDiario) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
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
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(obtenerColorTipo(entrada.tipo).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = obtenerEmojiTipo(entrada.tipo),
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = entrada.categoria,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto
                        )
                        Text(
                            text = formatearFecha(entrada.fecha),
                            fontSize = 12.sp,
                            color = GrisMedio
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (entrada.tipo == TipoTransaccion.INGRESO) "+" else "-"} S/ ${String.format("%.2f", entrada.monto)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (entrada.tipo == TipoTransaccion.INGRESO) VerdeMenta else CoralPastel
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val cambioSalud = entrada.impactoMascota.saludDespues - entrada.impactoMascota.saludAntes
                        Icon(
                            if (cambioSalud >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (cambioSalud >= 0) VerdeMenta else CoralPastel
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${if (cambioSalud >= 0) "+" else ""}$cambioSalud HP",
                            fontSize = 11.sp,
                            color = GrisMedio
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = GrisClaro)

                    Spacer(modifier = Modifier.height(12.dp))

                    if (entrada.descripcion.isNotEmpty()) {
                        Text(
                            text = entrada.descripcion,
                            fontSize = 13.sp,
                            color = GrisTexto,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = obtenerColorSalud(entrada.impactoMascota.saludDespues).copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = obtenerEmojiMascota(entrada.impactoMascota.saludDespues),
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entrada.mensajeMascota,
                                    fontSize = 13.sp,
                                    color = GrisTexto,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                                if (entrada.impactoMascota.experienciaGanada > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✨", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "+${entrada.impactoMascota.experienciaGanada} XP",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MoradoPrincipal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun obtenerColorTipo(tipo: TipoTransaccion): Color {
    return when (tipo) {
        TipoTransaccion.INGRESO -> VerdeMenta
        TipoTransaccion.GASTO -> CoralPastel
        TipoTransaccion.AHORRO -> AzulPastel
        TipoTransaccion.META -> MoradoPrincipal
    }
}

fun obtenerEmojiTipo(tipo: TipoTransaccion): String {
    return when (tipo) {
        TipoTransaccion.INGRESO -> "💰"
        TipoTransaccion.GASTO -> "💸"
        TipoTransaccion.AHORRO -> "🐷"
        TipoTransaccion.META -> "🎯"
    }
}

fun formatearFecha(fecha: Date): String {
    val formato = SimpleDateFormat("dd MMM, HH:mm", Locale("es", "ES"))
    return formato.format(fecha)
}

fun obtenerEntradasEjemplo(): List<EntradaDiario> {
    val calendario = Calendar.getInstance()
    return listOf(
        EntradaDiario(
            id = 1,
            fecha = calendario.time,
            tipo = TipoTransaccion.GASTO,
            monto = 45.50,
            categoria = "Alimentación",
            descripcion = "Compras en el supermercado",
            impactoMascota = ImpactoMascota(75, 72, 5),
            mensajeMascota = "¡Recuerda comparar precios! Cada sol cuenta 🐾"
        ),
        EntradaDiario(
            id = 2,
            fecha = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            tipo = TipoTransaccion.INGRESO,
            monto = 500.00,
            categoria = "Salario",
            descripcion = "Pago quincenal",
            impactoMascota = ImpactoMascota(72, 80, 20),
            mensajeMascota = "¡Excelente! ¡Me siento genial! 🌟"
        ),
        EntradaDiario(
            id = 3,
            fecha = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
            tipo = TipoTransaccion.AHORRO,
            monto = 100.00,
            categoria = "Fondo de emergencia",
            descripcion = "Ahorro mensual automático",
            impactoMascota = ImpactoMascota(80, 85, 30),
            mensajeMascota = "¡Qué orgulloso estoy! Ahorrar es crecer 💪"
        ),
        EntradaDiario(
            id = 4,
            fecha = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
            tipo = TipoTransaccion.GASTO,
            monto = 25.00,
            categoria = "Entretenimiento",
            descripcion = "Cine con amigos",
            impactoMascota = ImpactoMascota(85, 83, 5),
            mensajeMascota = "Está bien disfrutar, pero con medida 🎬"
        ),
        EntradaDiario(
            id = 5,
            fecha = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
            tipo = TipoTransaccion.GASTO,
            monto = 150.00,
            categoria = "Transporte",
            descripcion = "Recarga de combustible",
            impactoMascota = ImpactoMascota(83, 80, 5),
            mensajeMascota = "Considera opciones más económicas 🚗"
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewDiarioFinanciero() {
    MascotaFinancieraTheme {
        PantallaDiarioFinanciero()
    }
}