package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import com.lvmh.pocketpet.dominio.utilidades.PuntoGrafico
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

data class Estadisticas(
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val balance: Double = 0.0,
    val promedioGastos: Double = 0.0,
    val promedioGastoDiario: Double = 0.0,
    val categoriaConMasGastos: String = "",
    val transaccionesCount: Int = 0
)

data class ReporteMensual(
    val mes: Int,
    val anio: Int,
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val balance: Double = 0.0,
    val transaccionesPorDia: Map<Int, Double> = emptyMap(),
    val gastosPorCategoria: Map<String, Double> = emptyMap(),
    val ingresosPorCategoria: Map<String, Double> = emptyMap()
)

data class ComparacionPeriodo(
    val periodoActualIngresos: Double = 0.0,
    val periodoActualGastos: Double = 0.0,
    val periodoAnteriorIngresos: Double = 0.0,
    val periodoAnteriorGastos: Double = 0.0,
    val cambioIngresos: Double = 0.0,
    val cambioIngresosPorc: Double = 0.0,
    val cambioGastos: Double = 0.0,
    val cambioGastosPorc: Double = 0.0
)

data class EstadoEstadisticas(
    val cargando: Boolean = false,
    val estadisticas: Estadisticas? = null,
    val reporteMensual: ReporteMensual? = null,
    val comparacion: ComparacionPeriodo? = null,
    val datosGrafico: List<PuntoGrafico> = emptyList(),
    val periodoSeleccionado: String = "mes",
    val error: String? = null
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoEstadisticas())
    val estado: StateFlow<EstadoEstadisticas> = _estado.asStateFlow()

    private var transaccionesListener: ListenerRegistration? = null
    private var usuarioId: String? = null

    init {
        inicializar()
    }

    fun inicializar() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            usuarioId = userId
            escucharTransacciones(userId)
        } else {
            _estado.update { it.copy(error = "Usuario no autenticado") }
        }
    }

    private fun escucharTransacciones(userId: String) {
        transaccionesListener?.remove()
        _estado.update { it.copy(cargando = true) }

        println("🔵 [EstadisticasVM] Escuchando transacciones para estadísticas")

        transaccionesListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("transacciones")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ [EstadisticasVM] Error: ${error.message}")
                    _estado.update {
                        it.copy(cargando = false, error = "Error: ${error.message}")
                    }
                    return@addSnapshotListener
                }

                viewModelScope.launch {
                    val transacciones = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            Transaccion(
                                id = doc.id,
                                usuarioId = doc.getString("usuario_id") ?: userId,
                                tipo = TipoTransaccion.valueOf(doc.getString("tipo") ?: "GASTO"),
                                monto = doc.getDouble("monto") ?: 0.0,
                                categoriaId = doc.getString("categoria_id") ?: "",
                                categoriaNombre = doc.getString("categoria_nombre") ?: "",
                                categoriaEmoji = doc.getString("categoria_emoji") ?: "💰",
                                descripcion = doc.getString("descripcion") ?: "",
                                fecha = doc.getLong("fecha") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    println("✅ [EstadisticasVM] Transacciones cargadas: ${transacciones.size}")
                    procesarEstadisticas(transacciones)
                }
            }
    }

    private fun procesarEstadisticas(todasTransacciones: List<Transaccion>) {
        try {
            val (inicio, fin) = obtenerRangoFechas(_estado.value.periodoSeleccionado)

            // Filtrar transacciones por periodo
            val transacciones = todasTransacciones.filter {
                it.fecha in inicio..fin
            }

            println("🔵 [EstadisticasVM] Transacciones en periodo: ${transacciones.size}")

            val totalIngresos = transacciones
                .filter { it.tipo == TipoTransaccion.INGRESO }
                .sumOf { it.monto }

            val totalGastos = transacciones
                .filter { it.tipo == TipoTransaccion.GASTO }
                .sumOf { it.monto }

            val balance = totalIngresos - totalGastos

            val gastosPorCategoria = transacciones
                .filter { it.tipo == TipoTransaccion.GASTO }
                .groupBy { it.categoriaNombre }
                .mapValues { (_, trans) -> trans.sumOf { it.monto } }

            val categoriaConMasGastos = gastosPorCategoria
                .maxByOrNull { it.value }?.key ?: "Sin gastos"

            val promedioGastos = if (transacciones.isNotEmpty())
                totalGastos / transacciones.count { it.tipo == TipoTransaccion.GASTO }.coerceAtLeast(1)
            else 0.0

            val diasEnPeriodo = when (_estado.value.periodoSeleccionado) {
                "semana" -> 7
                "mes" -> 30
                "anio" -> 365
                else -> 1
            }
            val promedioGastoDiario = totalGastos / diasEnPeriodo

            val stats = Estadisticas(
                totalIngresos = totalIngresos,
                totalGastos = totalGastos,
                balance = balance,
                promedioGastos = promedioGastos,
                promedioGastoDiario = promedioGastoDiario,
                categoriaConMasGastos = categoriaConMasGastos,
                transaccionesCount = transacciones.size
            )

            // Generar datos para el gráfico
            val datosGrafico = generarDatosGrafico(transacciones)

            _estado.update {
                it.copy(
                    cargando = false,
                    estadisticas = stats,
                    datosGrafico = datosGrafico,
                    error = null
                )
            }

            println("✅ [EstadisticasVM] Estadísticas procesadas: Balance = S/.$balance")

        } catch (e: Exception) {
            println("❌ [EstadisticasVM] Error procesando: ${e.message}")
            _estado.update {
                it.copy(cargando = false, error = "Error: ${e.message}")
            }
        }
    }

    private fun generarDatosGrafico(transacciones: List<Transaccion>): List<PuntoGrafico> {
        return when (_estado.value.periodoSeleccionado) {
            "semana" -> generarGraficoSemanal(transacciones)
            "mes" -> generarGraficoMensual(transacciones)
            "anio" -> generarGraficoAnual(transacciones)
            else -> generarGraficoPorCategoria(transacciones)
        }
    }

    private fun generarGraficoSemanal(transacciones: List<Transaccion>): List<PuntoGrafico> {
        val calendar = Calendar.getInstance()
        val dias = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

        return (0..6).map { diaIndex ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -6 + diaIndex)

            val inicioDelDia = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val finDelDia = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            val gastoDelDia = transacciones
                .filter { it.tipo == TipoTransaccion.GASTO && it.fecha in inicioDelDia..finDelDia }
                .sumOf { it.monto }

            val diaActual = calendar.get(Calendar.DAY_OF_WEEK)
            val etiqueta = dias[(diaActual + 5) % 7]

            PuntoGrafico(etiqueta, gastoDelDia)
        }
    }

    private fun generarGraficoMensual(transacciones: List<Transaccion>): List<PuntoGrafico> {
        val calendar = Calendar.getInstance()
        val diasDelMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        return (1..diasDelMes).chunked(7).mapIndexed { index, dias ->
            val inicioSemana = calendar.apply {
                set(Calendar.DAY_OF_MONTH, dias.first())
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            val finSemana = calendar.apply {
                set(Calendar.DAY_OF_MONTH, dias.last())
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
            }.timeInMillis

            val gastoSemana = transacciones
                .filter { it.tipo == TipoTransaccion.GASTO && it.fecha in inicioSemana..finSemana }
                .sumOf { it.monto }

            PuntoGrafico("Sem ${index + 1}", gastoSemana)
        }
    }

    private fun generarGraficoAnual(transacciones: List<Transaccion>): List<PuntoGrafico> {
        val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

        return (0..11).map { mesIndex ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -11 + mesIndex)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val inicioMes = calendar.timeInMillis

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val finMes = calendar.timeInMillis

            val gastoMes = transacciones
                .filter { it.tipo == TipoTransaccion.GASTO && it.fecha in inicioMes..finMes }
                .sumOf { it.monto }

            PuntoGrafico(meses[calendar.get(Calendar.MONTH)], gastoMes)
        }
    }

    private fun generarGraficoPorCategoria(transacciones: List<Transaccion>): List<PuntoGrafico> {
        return transacciones
            .filter { it.tipo == TipoTransaccion.GASTO }
            .groupBy { it.categoriaNombre }
            .map { (categoria, trans) ->
                PuntoGrafico(categoria, trans.sumOf { it.monto })
            }
            .sortedByDescending { it.valor }
            .take(5)
    }

    fun cambiarPeriodo(periodo: String) {
        println("🔵 [EstadisticasVM] Cambiando periodo a: $periodo")
        _estado.update { it.copy(periodoSeleccionado = periodo) }
        usuarioId?.let { escucharTransacciones(it) }
    }

    fun cargarReporteMensual(mes: Int, anio: Int) {
        val userId = usuarioId ?: run {
            _estado.update { it.copy(error = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                println("🔵 [EstadisticasVM] Cargando reporte mensual: $mes/$anio")

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, anio)
                calendar.set(Calendar.MONTH, mes - 1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val inicioMes = calendar.timeInMillis

                val diasEnMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, diasEnMes)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val finMes = calendar.timeInMillis

                val snapshot = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereGreaterThanOrEqualTo("fecha", inicioMes)
                    .whereLessThanOrEqualTo("fecha", finMes)
                    .get()
                    .await()

                val transacciones = snapshot.documents.mapNotNull { doc ->
                    try {
                        Transaccion(
                            id = doc.id,
                            usuarioId = doc.getString("usuario_id") ?: userId,
                            tipo = TipoTransaccion.valueOf(doc.getString("tipo") ?: "GASTO"),
                            monto = doc.getDouble("monto") ?: 0.0,
                            categoriaId = doc.getString("categoria_id") ?: "",
                            categoriaNombre = doc.getString("categoria_nombre") ?: "",
                            categoriaEmoji = doc.getString("categoria_emoji") ?: "💰",
                            descripcion = doc.getString("descripcion") ?: "",
                            fecha = doc.getLong("fecha") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                println("✅ [EstadisticasVM] Transacciones del mes: ${transacciones.size}")

                val totalIngresos = transacciones
                    .filter { it.tipo == TipoTransaccion.INGRESO }
                    .sumOf { it.monto }

                val totalGastos = transacciones
                    .filter { it.tipo == TipoTransaccion.GASTO }
                    .sumOf { it.monto }

                val gastosPorCategoria = transacciones
                    .filter { it.tipo == TipoTransaccion.GASTO }
                    .groupBy { it.categoriaNombre }
                    .mapValues { (_, trans) -> trans.sumOf { it.monto } }

                val ingresosPorCategoria = transacciones
                    .filter { it.tipo == TipoTransaccion.INGRESO }
                    .groupBy { it.categoriaNombre }
                    .mapValues { (_, trans) -> trans.sumOf { it.monto } }

                val transaccionesPorDia = mutableMapOf<Int, Double>()
                transacciones.forEach { transaccion ->
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = transaccion.fecha
                    val dia = cal.get(Calendar.DAY_OF_MONTH)

                    val montoConSigno = if (transaccion.tipo == TipoTransaccion.INGRESO) {
                        transaccion.monto
                    } else {
                        -transaccion.monto
                    }

                    transaccionesPorDia[dia] = (transaccionesPorDia[dia] ?: 0.0) + montoConSigno
                }

                val reporte = ReporteMensual(
                    mes = mes,
                    anio = anio,
                    totalIngresos = totalIngresos,
                    totalGastos = totalGastos,
                    balance = totalIngresos - totalGastos,
                    transaccionesPorDia = transaccionesPorDia,
                    gastosPorCategoria = gastosPorCategoria,
                    ingresosPorCategoria = ingresosPorCategoria
                )

                _estado.update {
                    it.copy(
                        cargando = false,
                        reporteMensual = reporte,
                        error = null
                    )
                }

                println("✅ [EstadisticasVM] Reporte mensual cargado correctamente")

            } catch (e: Exception) {
                println("❌ [EstadisticasVM] Error cargando reporte: ${e.message}")
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = "Error al cargar reporte: ${e.message}"
                    )
                }
            }
        }
    }

    fun compararPeriodos() {
        val userId = usuarioId ?: run {
            _estado.update { it.copy(error = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                println("🔵 [EstadisticasVM] Comparando periodos")

                val (inicioActual, finActual) = obtenerRangoFechas(_estado.value.periodoSeleccionado)
                val (inicioAnterior, finAnterior) = obtenerRangoFechasAnterior(_estado.value.periodoSeleccionado)

                val transaccionesActuales = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereGreaterThanOrEqualTo("fecha", inicioActual)
                    .whereLessThanOrEqualTo("fecha", finActual)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        try {
                            Transaccion(
                                id = doc.id,
                                usuarioId = doc.getString("usuario_id") ?: userId,
                                tipo = TipoTransaccion.valueOf(doc.getString("tipo") ?: "GASTO"),
                                monto = doc.getDouble("monto") ?: 0.0,
                                categoriaId = doc.getString("categoria_id") ?: "",
                                categoriaNombre = doc.getString("categoria_nombre") ?: "",
                                categoriaEmoji = doc.getString("categoria_emoji") ?: "💰",
                                descripcion = doc.getString("descripcion") ?: "",
                                fecha = doc.getLong("fecha") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                val transaccionesAnteriores = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereGreaterThanOrEqualTo("fecha", inicioAnterior)
                    .whereLessThanOrEqualTo("fecha", finAnterior)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        try {
                            Transaccion(
                                id = doc.id,
                                usuarioId = doc.getString("usuario_id") ?: userId,
                                tipo = TipoTransaccion.valueOf(doc.getString("tipo") ?: "GASTO"),
                                monto = doc.getDouble("monto") ?: 0.0,
                                categoriaId = doc.getString("categoria_id") ?: "",
                                categoriaNombre = doc.getString("categoria_nombre") ?: "",
                                categoriaEmoji = doc.getString("categoria_emoji") ?: "💰",
                                descripcion = doc.getString("descripcion") ?: "",
                                fecha = doc.getLong("fecha") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                val ingresosActuales = transaccionesActuales
                    .filter { it.tipo == TipoTransaccion.INGRESO }
                    .sumOf { it.monto }

                val gastosActuales = transaccionesActuales
                    .filter { it.tipo == TipoTransaccion.GASTO }
                    .sumOf { it.monto }

                val ingresosAnteriores = transaccionesAnteriores
                    .filter { it.tipo == TipoTransaccion.INGRESO }
                    .sumOf { it.monto }

                val gastosAnteriores = transaccionesAnteriores
                    .filter { it.tipo == TipoTransaccion.GASTO }
                    .sumOf { it.monto }

                val cambioIngresos = ingresosActuales - ingresosAnteriores
                val cambioIngresosPorc = if (ingresosAnteriores > 0) {
                    (cambioIngresos / ingresosAnteriores) * 100
                } else 0.0

                val cambioGastos = gastosActuales - gastosAnteriores
                val cambioGastosPorc = if (gastosAnteriores > 0) {
                    (cambioGastos / gastosAnteriores) * 100
                } else 0.0

                val comparacion = ComparacionPeriodo(
                    periodoActualIngresos = ingresosActuales,
                    periodoActualGastos = gastosActuales,
                    periodoAnteriorIngresos = ingresosAnteriores,
                    periodoAnteriorGastos = gastosAnteriores,
                    cambioIngresos = cambioIngresos,
                    cambioIngresosPorc = cambioIngresosPorc,
                    cambioGastos = cambioGastos,
                    cambioGastosPorc = cambioGastosPorc
                )

                _estado.update {
                    it.copy(
                        cargando = false,
                        comparacion = comparacion,
                        error = null
                    )
                }

                println("✅ [EstadisticasVM] Comparación completada")

            } catch (e: Exception) {
                println("❌ [EstadisticasVM] Error comparando: ${e.message}")
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = "Error al comparar periodos: ${e.message}"
                    )
                }
            }
        }
    }

    private fun obtenerRangoFechasAnterior(periodo: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        when (periodo) {
            "semana" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val fin = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                return Pair(calendar.timeInMillis, fin)
            }
            "mes" -> {
                calendar.add(Calendar.MONTH, -1)
                val fin = calendar.timeInMillis
                calendar.add(Calendar.MONTH, -1)
                return Pair(calendar.timeInMillis, fin)
            }
            "anio" -> {
                calendar.add(Calendar.YEAR, -1)
                val fin = calendar.timeInMillis
                calendar.add(Calendar.YEAR, -1)
                return Pair(calendar.timeInMillis, fin)
            }
        }

        return Pair(0L, 0L)
    }

    private fun obtenerRangoFechas(periodo: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val fin = calendar.timeInMillis

        when (periodo) {
            "semana" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            "mes" -> calendar.add(Calendar.MONTH, -1)
            "anio" -> calendar.add(Calendar.YEAR, -1)
        }

        return Pair(calendar.timeInMillis, fin)
    }

    override fun onCleared() {
        super.onCleared()
        println("🔵 [EstadisticasVM] ViewModel limpiado")
        transaccionesListener?.remove()
    }
}