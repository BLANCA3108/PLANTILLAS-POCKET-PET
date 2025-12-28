package com.lvmh.pocketpet.dominio.casouso.estadisticas

import com.lvmh.pocketpet.datos.repositorios.TransactionRepository
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
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(
        usuarioId: String,
        inicioActual: Long,
        finActual: Long,
        inicioAnterior: Long,
        finAnterior: Long
    ): ComparacionPeriodo {

        val transaccionesActuales = transactionRepository.obtenerPorRangoFecha(usuarioId, inicioActual, finActual).first()
        val transaccionesAnteriores = transactionRepository.obtenerPorRangoFecha(usuarioId, inicioAnterior, finAnterior).first()

        val ingresosActuales = transaccionesActuales.filter { it.tipo == "ingreso" }.sumOf { it.monto }
        val gastosActuales = transaccionesActuales.filter { it.tipo == "gasto" }.sumOf { it.monto }

        val ingresosAnteriores = transaccionesAnteriores.filter { it.tipo == "ingreso" }.sumOf { it.monto }
        val gastosAnteriores = transaccionesAnteriores.filter { it.tipo == "gasto" }.sumOf { it.monto }

        val cambioIngresos = ingresosActuales - ingresosAnteriores
        val cambioGastos = gastosActuales - gastosAnteriores

        val cambioIngresosPorc = if (ingresosAnteriores > 0) (cambioIngresos / ingresosAnteriores) * 100 else 0.0
        val cambioGastosPorc = if (gastosAnteriores > 0) (cambioGastos / gastosAnteriores) * 100 else 0.0

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