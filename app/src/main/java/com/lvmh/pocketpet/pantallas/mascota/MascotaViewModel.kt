package com.lvmh.pocketpet.pantallas.mascota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.MascotaRepository
import com.lvmh.pocketpet.dominio.modelos.Mascota
import com.lvmh.pocketpet.dominio.modelos.TipoMascota
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MascotaViewModel(
    private val repository: MascotaRepository,
    private val usuarioId: String
) : ViewModel() {

    private val _estadoMascota = MutableStateFlow<EstadoMascota?>(null)
    val estadoMascota: StateFlow<EstadoMascota?> = _estadoMascota.asStateFlow()

    private val _primerIngreso = MutableStateFlow(true)
    val primerIngreso: StateFlow<Boolean> = _primerIngreso.asStateFlow()

    private val _debeMostrarPantallaPrincipal = MutableStateFlow(false)
    val debeMostrarPantallaPrincipal: StateFlow<Boolean> = _debeMostrarPantallaPrincipal.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajeError = MutableStateFlow("")
    val mensajeError: StateFlow<String> = _mensajeError.asStateFlow()

    private val _monedasDisponibles = MutableStateFlow(0)
    val monedasDisponibles: StateFlow<Int> = _monedasDisponibles.asStateFlow()

    init {
        cargarMascota()
    }

    // ❌ PROBLEMA ANTERIOR: Usaba .collect que nunca termina
    // ✅ SOLUCIÓN: Usar launch separado para collect
    private fun cargarMascota() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                // Usamos collect en un launch para que no bloquee
                repository.obtenerMascota(usuarioId).collect { mascota ->
                    _cargando.value = false // Mover aquí para que se ejecute cuando lleguen datos

                    if (mascota != null) {
                        val estadoUI = mascota.toEstadoUI()
                        _estadoMascota.value = estadoUI
                        _monedasDisponibles.value = estadoUI.monedasDisponibles
                        _primerIngreso.value = false
                        _debeMostrarPantallaPrincipal.value = false // No mostrar automáticamente
                    } else {
                        _primerIngreso.value = true
                        _debeMostrarPantallaPrincipal.value = false
                    }
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error al cargar mascota: ${e.message}"
                _primerIngreso.value = true
                _debeMostrarPantallaPrincipal.value = false
                _cargando.value = false
            }
        }
    }

    fun seleccionarMascota(tipo: String, nombre: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = "" // Limpiar errores previos

            try {
                val tipoMascota = when (tipo) {
                    "🐶" -> TipoMascota.PERRO
                    "🐱" -> TipoMascota.GATO
                    "🐰" -> TipoMascota.CONEJO
                    "🐢" -> TipoMascota.PERRO // Puedes crear TORTUGA en TipoMascota
                    "🦄" -> TipoMascota.GATO  // Puedes crear UNICORNIO en TipoMascota
                    else -> TipoMascota.PERRO
                }

                val nuevaMascota = Mascota(
                    usuarioId = usuarioId,
                    nombre = nombre.ifBlank { "Mi Mascota" }, // Validar nombre vacío
                    emoji = tipo,
                    tipo = tipoMascota,
                    nivel = 1,
                    experiencia = 0,
                    salud = 0.75f,
                    felicidad = 0.80f,
                    hambre = 0.6f,
                    energia = 0.7f,
                    monedas = 100
                )

                val resultado = repository.crearMascota(nuevaMascota)

                if (resultado.isSuccess) {
                    // Actualizar el estado directamente
                    val estadoCreado = nuevaMascota.toEstadoUI()
                    _estadoMascota.value = estadoCreado
                    _monedasDisponibles.value = estadoCreado.monedasDisponibles
                    _primerIngreso.value = false
                    _debeMostrarPantallaPrincipal.value = true
                    _mensajeError.value = ""
                } else {
                    _mensajeError.value = "Error al crear mascota: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun mascotaSorpresa(nombre: String) {
        val tipos = listOf("🐶", "🐱", "🐰", "🐢", "🦄")
        val tipoAleatorio = tipos.random()
        seleccionarMascota(tipoAleatorio, nombre)
    }

    fun resetearNavegacion() {
        _debeMostrarPantallaPrincipal.value = false
    }

    // ✅ AGREGAR ESTA FUNCIÓN QUE FALTABA
    fun limpiarMensajeError() {
        _mensajeError.value = ""
    }

    fun alimentarMascota() {
        viewModelScope.launch {
            _estadoMascota.value?.let { estado ->
                try {
                    repository.alimentarMascota(estado.id).onSuccess {
                        _estadoMascota.value = estado.copy(
                            hambre = 0,
                            salud = (estado.salud + 10).coerceAtMost(100)
                        )
                    }.onFailure { error ->
                        _mensajeError.value = "Error al alimentar: ${error.message}"
                    }
                } catch (e: Exception) {
                    _mensajeError.value = e.message ?: "Error al alimentar"
                }
            }
        }
    }

    fun jugarConMascota(monedasGanadas: Int = 50) {
        viewModelScope.launch {
            _estadoMascota.value?.let { estado ->
                try {
                    val mascotaActualizada = Mascota(
                        usuarioId = usuarioId,
                        nombre = estado.nombre,
                        emoji = estado.tipo,
                        tipo = obtenerTipoDesdeEmoji(estado.tipo),
                        nivel = estado.nivel,
                        experiencia = estado.experiencia + 50,
                        salud = estado.salud.toFloat() / 100,
                        felicidad = ((estado.felicidad + 20).coerceAtMost(100)).toFloat() / 100,
                        hambre = (estado.hambre - 10).coerceAtLeast(0).toFloat() / 100,
                        energia = ((estado.energia - 15).coerceAtLeast(0)).toFloat() / 100,
                        monedas = estado.monedasDisponibles + monedasGanadas
                    )

                    repository.actualizarMascota(mascotaActualizada).onSuccess {
                        _estadoMascota.value = mascotaActualizada.toEstadoUI()
                        _monedasDisponibles.value = mascotaActualizada.monedas
                    }.onFailure { error ->
                        _mensajeError.value = "Error al jugar: ${error.message}"
                    }
                } catch (e: Exception) {
                    _mensajeError.value = e.message ?: "Error al jugar"
                }
            }
        }
    }

    fun gastarMonedas(cantidad: Int): Boolean {
        return if ((_estadoMascota.value?.monedasDisponibles ?: 0) >= cantidad) {
            viewModelScope.launch {
                _estadoMascota.value?.let { estado ->
                    try {
                        val mascotaActualizada = Mascota(
                            usuarioId = usuarioId,
                            nombre = estado.nombre,
                            emoji = estado.tipo,
                            tipo = obtenerTipoDesdeEmoji(estado.tipo),
                            nivel = estado.nivel,
                            experiencia = estado.experiencia,
                            salud = estado.salud.toFloat() / 100,
                            felicidad = estado.felicidad.toFloat() / 100,
                            hambre = estado.hambre.toFloat() / 100,
                            energia = estado.energia.toFloat() / 100,
                            monedas = estado.monedasDisponibles - cantidad
                        )

                        repository.actualizarMascota(mascotaActualizada).onSuccess {
                            _estadoMascota.value = mascotaActualizada.toEstadoUI()
                            _monedasDisponibles.value = mascotaActualizada.monedas
                        }
                    } catch (e: Exception) {
                        _mensajeError.value = e.message ?: "Error al gastar monedas"
                    }
                }
            }
            true
        } else {
            false
        }
    }

    private fun obtenerTipoDesdeEmoji(emoji: String): TipoMascota {
        return when (emoji) {
            "🐶" -> TipoMascota.PERRO
            "🐱" -> TipoMascota.GATO
            "🐰" -> TipoMascota.CONEJO
            else -> TipoMascota.PERRO
        }
    }

    private fun Mascota.toEstadoUI(): EstadoMascota {
        return EstadoMascota(
            id = this.id ?: "",
            nombre = this.nombre,
            tipo = this.emoji,
            salud = (this.salud * 100).toInt(),
            nivel = this.nivel,
            experiencia = this.experiencia,
            experienciaMax = this.nivel * 1000,
            hambre = (this.hambre * 100).toInt(),
            felicidad = (this.felicidad * 100).toInt(),
            energia = (this.energia * 100).toInt(),
            monedasDisponibles = this.monedas
        )
    }
}

class MascotaViewModelFactory(
    private val repository: MascotaRepository,
    private val usuarioId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MascotaViewModel(repository, usuarioId) as T
    }
}

/*
EXPLICACIÓN DE LOS CAMBIOS:

1. ✅ cargarMascota():
   - El problema era que .collect nunca termina porque es un Flow infinito
   - Solución: Mover _cargando.value = false dentro del collect
   - Así deja de mostrar "cargando" cuando recibe el primer dato

2. ✅ limpiarMensajeError():
   - Función que faltaba para limpiar mensajes de error
   - La necesitas para cerrar el diálogo de error

3. ✅ seleccionarMascota():
   - Agregado .ifBlank { "Mi Mascota" } para nombres vacíos
   - Limpia errores previos al inicio con _mensajeError.value = ""
   - Establece _cargando.value = false en el finally

4. ✅ Actualización de monedas:
   - Ahora actualiza _monedasDisponibles en jugarConMascota y gastarMonedas
   - Esto mantiene sincronizado el contador de monedas en el TopBar
*/