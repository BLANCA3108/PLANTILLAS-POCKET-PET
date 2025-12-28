package com.lvmh.pocketpet.dominio.utilidades

import com.lvmh.pocketpet.dominio.modelos.Transaction
import java.util.*

data class PuntoGrafico(val etiqueta: String, val valor: Double)

object AsistenteGraficos {

    fun prepararDatosBarras(transacciones: List<Transaction>): List<PuntoGrafico> {
        return transacciones.groupBy { it.categoriaId }
            .map { PuntoGrafico(it.key, it.value.sumOf { t -> t.monto }) }
            .sortedByDescending { it.valor }
    }

    fun prepararDatosPastel(transacciones: List<Transaction>): List<PuntoGrafico> {
        val total = transacciones.sumOf { it.monto }
        if (total == 0.0) return emptyList()

        return transacciones.groupBy { it.categoriaId }
            .map {
                val porcentaje = (it.value.sumOf { t -> t.monto } / total) * 100
                PuntoGrafico(it.key, porcentaje)
            }
            .sortedByDescending { it.valor }
    }

    fun prepararDatosLinea(transacciones: List<Transaction>, dias: Int): List<PuntoGrafico> {
        val calendar = Calendar.getInstance()
        val ahora = System.currentTimeMillis()

        return (0 until dias).map { dia ->
            calendar.timeInMillis = ahora - (dia * 24 * 60 * 60 * 1000L)
            val diaTexto = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}"

            val transaccionesDia = transacciones.filter {
                val calTrans = Calendar.getInstance().apply { timeInMillis = it.fecha }
                calTrans.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                        calTrans.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
            }

            PuntoGrafico(diaTexto, transaccionesDia.sumOf { it.monto })
        }.reversed()
    }

    fun prepararDatosMensuales(transacciones: List<Transaction>, meses: Int): List<PuntoGrafico> {
        val calendar = Calendar.getInstance()

        return (0 until meses).map { mes ->
            calendar.add(Calendar.MONTH, -1)
            val mesTexto = obtenerNombreMes(calendar.get(Calendar.MONTH))

            val transaccionesMes = transacciones.filter {
                val calTrans = Calendar.getInstance().apply { timeInMillis = it.fecha }
                calTrans.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        calTrans.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
            }

            PuntoGrafico(mesTexto, transaccionesMes.sumOf { it.monto })
        }.reversed()
    }

    private fun obtenerNombreMes(mes: Int): String {
        return when (mes) {
            0 -> "Ene"
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Abr"
            4 -> "May"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Ago"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            11 -> "Dic"
            else -> ""
        }
    }
}
