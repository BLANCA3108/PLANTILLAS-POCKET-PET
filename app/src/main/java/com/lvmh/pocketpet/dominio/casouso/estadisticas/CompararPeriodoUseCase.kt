package com.lvmh.pocketpet.dominio.casouso.estadisticas

import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import kotlinx.coroutines.flow.first

data class ComparacionPeriodo(
    val periodoActualIngresos: Double,
    val periodoAnteriorIngresos: Double,
    val periodoActualGastos: Double,
    val periodoAnteriorGastos: Double,
    val cambioIngresos: Double,
    val cambioGastos: Double,
    val cambioIngresosPorc: Double,
    val cambioGastosPorc: Double
)

class CompararPeriodoUseCase(
    private val transaccionRepository: TransaccionRepository
) {

    suspend operator fun invoke(
        usuarioId: String,
        inicioActual: Long,
        finActual: Long,
        inicioAnterior: Long,
        finAnterior: Long
    ): ComparacionPeriodo {

        val actuales = transaccionRepository
            .obtenerTransaccionesPorRangoFecha(usuarioId, inicioActual, finActual)
            .first()

        val anteriores = transaccionRepository
            .obtenerTransaccionesPorRangoFecha(usuarioId, inicioAnterior, finAnterior)
            .first()

        val ingresosActuales = actuales
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .sumOf { it.monto }

        val gastosActuales = actuales
            .filter { it.tipo == TipoTransaccion.GASTO }
            .sumOf { it.monto }

        val ingresosAnteriores = anteriores
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .sumOf { it.monto }

        val gastosAnteriores = anteriores
            .filter { it.tipo == TipoTransaccion.GASTO }
            .sumOf { it.monto }

        val cambioIngresos = ingresosActuales - ingresosAnteriores
        val cambioGastos = gastosActuales - gastosAnteriores

        val cambioIngresosPorc =
            if (ingresosAnteriores > 0) (cambioIngresos / ingresosAnteriores) * 100 else 0.0

        val cambioGastosPorc =
            if (gastosAnteriores > 0) (cambioGastos / gastosAnteriores) * 100 else 0.0

        return ComparacionPeriodo(
            periodoActualIngresos = ingresosActuales,
            periodoAnteriorIngresos = ingresosAnteriores,
            periodoActualGastos = gastosActuales,
            periodoAnteriorGastos = gastosAnteriores,
            cambioIngresos = cambioIngresos,
            cambioGastos = cambioGastos,
            cambioIngresosPorc = cambioIngresosPorc,
            cambioGastosPorc = cambioGastosPorc
        )
    }
}
