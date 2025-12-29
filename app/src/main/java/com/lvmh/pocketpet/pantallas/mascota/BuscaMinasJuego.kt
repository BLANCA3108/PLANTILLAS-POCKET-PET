package com.example.mascotafinanciera.pantallas.juegos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Celda(
    val fila: Int,
    val columna: Int,
    val esMina: Boolean = false,
    var revelada: Boolean = false,
    var marcada: Boolean = false,
    var minasAdyacentes: Int = 0
)

data class EstadoJuegoBuscaMinas(
    val dificultad: DificultadBuscaMinas = DificultadBuscaMinas.FACIL,
    val tablero: List<List<Celda>> = emptyList(),
    val juegoIniciado: Boolean = false,
    val juegoTerminado: Boolean = false,
    val victoria: Boolean = false,
    val celdasReveladas: Int = 0,
    val marcas: Int = 0,
    val tiempo: Int = 0
)

enum class DificultadBuscaMinas(
    val filas: Int,
    val columnas: Int,
    val minas: Int,
    val emoji: String,
    val color: Color
) {
    FACIL(8, 8, 10, "😊", VerdeMenta),
    MEDIO(10, 10, 20, "🤔", AmarilloPastel),
    DIFICIL(12, 12, 30, "😰", CoralPastel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBuscaMinasJuego() {
    var estadoJuego by remember { mutableStateOf(EstadoJuegoBuscaMinas()) }
    var modoMarcar by remember { mutableStateOf(false) }

    LaunchedEffect(estadoJuego.juegoIniciado, estadoJuego.juegoTerminado) {
        if (estadoJuego.juegoIniciado && !estadoJuego.juegoTerminado) {
            delay(1000)
            estadoJuego = estadoJuego.copy(tiempo = estadoJuego.tiempo + 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Buscaminas",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("💣", fontSize = 20.sp)
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
                    containerColor = AzulPastel,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp)
        ) {
            if (!estadoJuego.juegoIniciado) {
                PantallaSeleccionDificultad(
                    onDificultadSeleccionada = { dificultad ->
                        val nuevoTablero = generarTablero(
                            dificultad.filas,
                            dificultad.columnas,
                            dificultad.minas
                        )
                        estadoJuego = EstadoJuegoBuscaMinas(
                            dificultad = dificultad,
                            tablero = nuevoTablero,
                            juegoIniciado = true
                        )
                    }
                )
            } else if (estadoJuego.juegoTerminado) {
                PantallaFinBuscaMinas(
                    victoria = estadoJuego.victoria,
                    tiempo = estadoJuego.tiempo,
                    dificultad = estadoJuego.dificultad,
                    onReintentar = {
                        val nuevoTablero = generarTablero(
                            estadoJuego.dificultad.filas,
                            estadoJuego.dificultad.columnas,
                            estadoJuego.dificultad.minas
                        )
                        estadoJuego = EstadoJuegoBuscaMinas(
                            dificultad = estadoJuego.dificultad,
                            tablero = nuevoTablero,
                            juegoIniciado = true
                        )
                    },
                    onCambiarDificultad = {
                        estadoJuego = EstadoJuegoBuscaMinas()
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HUDBuscaMinas(
                        tiempo = estadoJuego.tiempo,
                        marcas = estadoJuego.marcas,
                        minasTotal = estadoJuego.dificultad.minas,
                        modoMarcar = modoMarcar,
                        onToggleModo = { modoMarcar = !modoMarcar }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TableroJuego(
                        tablero = estadoJuego.tablero,
                        modoMarcar = modoMarcar,
                        onCeldaClick = { celda ->
                            if (modoMarcar) {
                                val nuevoTablero = estadoJuego.tablero.map { fila ->
                                    fila.map { c ->
                                        if (c.fila == celda.fila && c.columna == celda.columna && !c.revelada) {
                                            val nuevaMarca = !c.marcada
                                            c.copy(marcada = nuevaMarca).also {
                                                estadoJuego = estadoJuego.copy(
                                                    marcas = if (nuevaMarca) estadoJuego.marcas + 1 else estadoJuego.marcas - 1
                                                )
                                            }
                                        } else c
                                    }
                                }
                                estadoJuego = estadoJuego.copy(tablero = nuevoTablero)
                            } else {
                                if (!celda.marcada && !celda.revelada) {
                                    if (celda.esMina) {
                                        val tableroRevelado = revelarTodo(estadoJuego.tablero)
                                        estadoJuego = estadoJuego.copy(
                                            tablero = tableroRevelado,
                                            juegoTerminado = true,
                                            victoria = false
                                        )
                                    } else {
                                        val nuevoTablero = revelarCelda(estadoJuego.tablero, celda)
                                        val celdasReveladas = contarCeldasReveladas(nuevoTablero)
                                        val totalCeldas = estadoJuego.dificultad.filas * estadoJuego.dificultad.columnas
                                        val celdasSeguras = totalCeldas - estadoJuego.dificultad.minas

                                        if (celdasReveladas >= celdasSeguras) {

                                            estadoJuego = estadoJuego.copy(
                                                tablero = nuevoTablero,
                                                juegoTerminado = true,
                                                victoria = true,
                                                celdasReveladas = celdasReveladas
                                            )
                                        } else {
                                            estadoJuego = estadoJuego.copy(
                                                tablero = nuevoTablero,
                                                celdasReveladas = celdasReveladas
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HUDBuscaMinas(
    tiempo: Int,
    marcas: Int,
    minasTotal: Int,
    modoMarcar: Boolean,
    onToggleModo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tiempo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⏱️",
                    fontSize = 24.sp
                )
                Text(
                    text = "${tiempo}s",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "💣",
                    fontSize = 24.sp
                )
                Text(
                    text = "${minasTotal - marcas}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (marcas > minasTotal) CoralPastel else GrisTexto
                )
            }

            Button(
                onClick = onToggleModo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (modoMarcar) CoralPastel else AzulPastel
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (modoMarcar) "🚩 Marcar" else "👆 Revelar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TableroJuego(
    tablero: List<List<Celda>>,
    modoMarcar: Boolean,
    onCeldaClick: (Celda) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = GrisClaro),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(tablero[0].size),
            modifier = Modifier.padding(4.dp)
        ) {
            items(tablero.flatten()) { celda ->
                CeldaItem(
                    celda = celda,
                    onClick = { onCeldaClick(celda) }
                )
            }
        }
    }
}

@Composable
fun CeldaItem(
    celda: Celda,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        celda.revelada && celda.esMina -> CoralPastel
        celda.revelada -> Color.White
        else -> AzulCielo.copy(alpha = 0.5f)
    }

    val textColor = when (celda.minasAdyacentes) {
        1 -> Color.Blue
        2 -> VerdeMenta
        3 -> CoralPastel
        4 -> Color(0xFF000080) // Azul oscuro
        5 -> Color(0xFF800000) // Rojo oscuro
        else -> GrisTexto
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = if (celda.revelada) GrisClaro else GrisMedio.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            celda.marcada && !celda.revelada -> {
                Text(
                    text = "🚩",
                    fontSize = 16.sp
                )
            }
            celda.revelada && celda.esMina -> {
                Text(
                    text = "💣",
                    fontSize = 16.sp
                )
            }
            celda.revelada && celda.minasAdyacentes > 0 -> {
                Text(
                    text = "${celda.minasAdyacentes}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun BoxScope.PantallaSeleccionDificultad(
    onDificultadSeleccionada: (DificultadBuscaMinas) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💣",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Buscaminas",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AzulPastel
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Encuentra todas las celdas seguras",
            fontSize = 14.sp,
            color = GrisMedio,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Selecciona la Dificultad:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

        Spacer(modifier = Modifier.height(16.dp))

        DificultadBuscaMinas.entries.forEach { dificultad ->
            TarjetaDificultad(
                dificultad = dificultad,
                onClick = { onDificultadSeleccionada(dificultad) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cómo Jugar:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Toca para revelar celdas\n• Usa el modo marcar para señalar minas\n• Los números indican minas adyacentes\n• ¡Evita las bombas!",
                    fontSize = 12.sp,
                    color = GrisMedio,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun TarjetaDificultad(
    dificultad: DificultadBuscaMinas,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
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
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(dificultad.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dificultad.emoji,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = dificultad.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )
                    Text(
                        text = "${dificultad.filas}x${dificultad.columnas} • ${dificultad.minas} minas",
                        fontSize = 12.sp,
                        color = GrisMedio
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = dificultad.color
            )
        }
    }
}

@Composable
fun BoxScope.PantallaFinBuscaMinas(
    victoria: Boolean,
    tiempo: Int,
    dificultad: DificultadBuscaMinas,
    onReintentar: () -> Unit,
    onCambiarDificultad: () -> Unit
) {
    val monedasGanadas = if (victoria) {
        when (dificultad) {
            DificultadBuscaMinas.FACIL -> 20
            DificultadBuscaMinas.MEDIO -> 40
            DificultadBuscaMinas.DIFICIL -> 80
        }
    } else 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (victoria) "🎉" else "💥",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (victoria) "¡Victoria!" else "¡Boom!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (victoria) VerdeMenta else CoralPastel
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
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
                if (victoria) {
                    Text(
                        text = "Tiempo: ${tiempo}s",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPastel
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dificultad: ${dificultad.name}",
                        fontSize = 14.sp,
                        color = GrisMedio
                    )
                } else {
                    Text(
                        text = "¡Pisaste una mina!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tiempo sobrevivido: ${tiempo}s",
                        fontSize = 14.sp,
                        color = GrisMedio
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = GrisClaro)

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmarilloPastel.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "+$monedasGanadas",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmarilloPastel
                            )
                            Text(
                                text = "Monedas ganadas",
                                fontSize = 12.sp,
                                color = GrisMedio
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onReintentar,
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulPastel
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reintentar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCambiarDificultad,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = GrisMedio
            )
        ) {
            Text(
                text = "Cambiar Dificultad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun generarTablero(filas: Int, columnas: Int, numMinas: Int): List<List<Celda>> {
    val tablero = MutableList(filas) { fila ->
        MutableList(columnas) { columna ->
            Celda(fila, columna)
        }
    }

    // Colocar minas aleatoriamente
    var minasColocadas = 0
    while (minasColocadas < numMinas) {
        val fila = Random.nextInt(filas)
        val columna = Random.nextInt(columnas)

        if (!tablero[fila][columna].esMina) {
            tablero[fila][columna] = tablero[fila][columna].copy(esMina = true)
            minasColocadas++
        }
    }

    // Calcular minas adyacentes
    for (fila in 0 until filas) {
        for (columna in 0 until columnas) {
            if (!tablero[fila][columna].esMina) {
                var conteo = 0
                for (f in (fila - 1)..(fila + 1)) {
                    for (c in (columna - 1)..(columna + 1)) {
                        if (f in 0 until filas && c in 0 until columnas && tablero[f][c].esMina) {
                            conteo++
                        }
                    }
                }
                tablero[fila][columna] = tablero[fila][columna].copy(minasAdyacentes = conteo)
            }
        }
    }

    return tablero
}

fun revelarCelda(tablero: List<List<Celda>>, celda: Celda): List<List<Celda>> {
    val nuevoTablero = tablero.map { it.toMutableList() }.toMutableList()
    val fila = celda.fila
    val columna = celda.columna

    if (fila !in tablero.indices || columna !in tablero[0].indices) return tablero
    if (nuevoTablero[fila][columna].revelada || nuevoTablero[fila][columna].esMina) return tablero

    nuevoTablero[fila][columna] = nuevoTablero[fila][columna].copy(revelada = true)

    // Si no hay minas adyacentes, revelar celdas vecinas
    if (nuevoTablero[fila][columna].minasAdyacentes == 0) {
        for (f in (fila - 1)..(fila + 1)) {
            for (c in (columna - 1)..(columna + 1)) {
                if (f in tablero.indices && c in tablero[0].indices) {
                    if (!nuevoTablero[f][c].revelada && !nuevoTablero[f][c].esMina) {
                        return revelarCelda(nuevoTablero, nuevoTablero[f][c])
                    }
                }
            }
        }
    }

    return nuevoTablero
}

fun revelarTodo(tablero: List<List<Celda>>): List<List<Celda>> {
    return tablero.map { fila ->
        fila.map { celda ->
            celda.copy(revelada = true)
        }
    }
}

fun contarCeldasReveladas(tablero: List<List<Celda>>): Int {
    return tablero.flatten().count { it.revelada }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBuscaMinasJuego() {
    MascotaFinancieraTheme {
        PantallaBuscaMinasJuego()
    }
}