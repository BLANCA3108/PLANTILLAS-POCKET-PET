package com.lvmh.pocketpet.dominio.casouso.estadisticas

import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import kotlinx.coroutines.flow.first

data class Estadisticas(
    val totalIngresos: Double,
    val totalGastos: Double,
    val balance: Double,
    val transaccionMasAlta: Transaccion?,
    val categoriaConMasGastos: String,
    val promedioGastoDiario: Double
)

class CalcularEstadisticasUseCase(
    private val transaccionRepository: TransaccionRepository
) {

    suspend operator fun invoke(
        usuarioId: String,
        mesInicio: Long,
        mesFin: Long
    ): Estadisticas {

        val transacciones =
            transaccionRepository
                .obtenerTransaccionesPorRangoFecha(usuarioId, mesInicio, mesFin)
                .first()

        val ingresos = transacciones.filter { it.tipo == TipoTransaccion.INGRESO }
        val gastos   = transacciones.filter { it.tipo == TipoTransaccion.GASTO }


        val totalIngresos = ingresos.sumOf { it.monto }
        val totalGastos = gastos.sumOf { it.monto }
        val balance = totalIngresos - totalGastos

        val transaccionMasAlta = transacciones.maxByOrNull { it.monto }

        val categoriaConMasGastos = gastos
            .groupBy { it.categoriaId }
            .maxByOrNull { it.value.sumOf { t -> t.monto } }
            ?.key ?: ""

        val dias = ((mesFin - mesInicio) / (1000 * 60 * 60 * 24))
            .toInt()
            .coerceAtLeast(1)

        val promedioGastoDiario = totalGastos / dias

        return Estadisticas(
            totalIngresos,
            totalGastos,
            balance,
            transaccionMasAlta,
            categoriaConMasGastos,
            promedioGastoDiario
        )
    }
}
