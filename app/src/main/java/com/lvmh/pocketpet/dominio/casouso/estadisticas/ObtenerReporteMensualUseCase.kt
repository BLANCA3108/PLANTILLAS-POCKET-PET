package com.lvmh.pocketpet.dominio.casouso.estadisticas

import com.lvmh.pocketpet.datos.repositorios.TransactionRepository
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
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(usuarioId: String, mes: Int, anio: Int): ReporteMensual {
        val calendar = Calendar.getInstance()
        calendar.set(anio, mes - 1, 1, 0, 0, 0)
        val inicio = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val fin = calendar.timeInMillis

        val transacciones = transactionRepository.obtenerPorRangoFecha(usuarioId, inicio, fin).first()

        val ingresos = transacciones.filter { it.tipo == "ingreso" }
        val gastos = transacciones.filter { it.tipo == "gasto" }

        val ingresosPorCategoria = ingresos.groupBy { it.categoriaId }
            .mapValues { it.value.sumOf { t -> t.monto } }

        val gastosPorCategoria = gastos.groupBy { it.categoriaId }
            .mapValues { it.value.sumOf { t -> t.monto } }

        val transaccionesPorDia = transacciones.groupBy {
            Calendar.getInstance().apply { timeInMillis = it.fecha }.get(Calendar.DAY_OF_MONTH)
        }.mapValues { it.value.sumOf { t -> if (t.tipo == "ingreso") t.monto else -t.monto } }

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