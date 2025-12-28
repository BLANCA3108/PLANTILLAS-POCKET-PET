package com.lvmh.pocketpet.dominio.casouso.estadisticas

import com.lvmh.pocketpet.datos.repositorios.TransactionRepository
import com.lvmh.pocketpet.dominio.modelos.Transaction
import kotlinx.coroutines.flow.first

data class Estadisticas(
    val totalIngresos: Double,
    val totalGastos: Double,
    val balance: Double,
    val transaccionMasAlta: Transaction?,
    val categoriaConMasGastos: String,
    val promedioGastoDiario: Double
)

class CalcularEstadisticasUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(usuarioId: String, mesInicio: Long, mesFin: Long): Estadisticas {
        val transacciones = transactionRepository.obtenerPorRangoFecha(usuarioId, mesInicio, mesFin).first()

        val ingresos = transacciones.filter { it.tipo == "ingreso" }
        val gastos = transacciones.filter { it.tipo == "gasto" }

        val totalIngresos = ingresos.sumOf { it.monto }
        val totalGastos = gastos.sumOf { it.monto }
        val balance = totalIngresos - totalGastos

        val transaccionMasAlta = transacciones.maxByOrNull { it.monto }

        val categoriaConMasGastos = gastos
            .groupBy { it.categoriaId }
            .maxByOrNull { it.value.sumOf { t -> t.monto } }
            ?.key ?: ""

        val dias = ((mesFin - mesInicio) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
        val promedioGastoDiario = totalGastos / dias

        return Estadisticas(
            totalIngresos = totalIngresos,
            totalGastos = totalGastos,
            balance = balance,
            transaccionMasAlta = transaccionMasAlta,
            categoriaConMasGastos = categoriaConMasGastos,
            promedioGastoDiario = promedioGastoDiario
        )
    }
}