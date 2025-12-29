package com.lvmh.pocketpet.dominio.casouso.estadisticas

import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import kotlinx.coroutines.flow.first
import java.util.*

data class ReporteMensual(
    val mes: Int,
    val anio: Int,
    val ingresosPorCategoria: Map<String, Double>,
    val gastosPorCategoria: Map<String, Double>,
    val transaccionesPorDia: Map<Int, Double>,
    val totalIngresos: Double,
    val totalGastos: Double
)

class ObtenerReporteMensualUseCase(
    private val transaccionRepository: TransaccionRepository
) {

    suspend operator fun invoke(
        usuarioId: String,
        mes: Int,
        anio: Int
    ): ReporteMensual {

        val calendar = Calendar.getInstance()
        calendar.set(anio, mes - 1, 1, 0, 0, 0)
        val inicio = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val fin = calendar.timeInMillis

        val transacciones = transaccionRepository
            .obtenerTransaccionesPorRangoFecha(usuarioId, inicio, fin)
            .first()

        val ingresos = transacciones.filter { it.tipo == TipoTransaccion.INGRESO }
        val gastos = transacciones.filter { it.tipo == TipoTransaccion.GASTO }

        val ingresosPorCategoria = ingresos
            .groupBy { it.categoriaId }
            .mapValues { (_, lista) -> lista.sumOf { it.monto } }

        val gastosPorCategoria = gastos
            .groupBy { it.categoriaId }
            .mapValues { (_, lista) -> lista.sumOf { it.monto } }

        val transaccionesPorDia = transacciones
            .groupBy {
                Calendar.getInstance().apply {
                    timeInMillis = it.fecha
                }.get(Calendar.DAY_OF_MONTH)
            }
            .mapValues { (_, lista) ->
                lista.sumOf {
                    if (it.tipo == TipoTransaccion.INGRESO) it.monto else -it.monto
                }
            }

        return ReporteMensual(
            mes = mes,
            anio = anio,
            ingresosPorCategoria = ingresosPorCategoria,
            gastosPorCategoria = gastosPorCategoria,
            transaccionesPorDia = transaccionesPorDia,
            totalIngresos = ingresos.sumOf { it.monto },
            totalGastos = gastos.sumOf { it.monto }
        )
    }
}
