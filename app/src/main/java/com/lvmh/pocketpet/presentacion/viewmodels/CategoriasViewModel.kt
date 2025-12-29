package com.lvmh.pocketpet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.CategoriaRepository
import com.lvmh.pocketpet.dominio.modelos.Categoria
import com.lvmh.pocketpet.dominio.modelos.TipoCategoria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    // Estado de las categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Filtro por tipo
    private val _tipoFiltro = MutableStateFlow<TipoCategoria?>(null)
    val tipoFiltro: StateFlow<TipoCategoria?> = _tipoFiltro.asStateFlow()

    // Categorías filtradas
    val categoriasFiltradas: StateFlow<List<Categoria>> = combine(
        _categorias,
        _tipoFiltro
    ) { categorias, tipo ->
        if (tipo == null) categorias
        else categorias.filter { it.tipo == tipo }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Usuario ID (deberías obtenerlo de Firebase Auth)
    private var usuarioId: String = ""

    fun inicializar(userId: String) {
        usuarioId = userId
        cargarCategorias()
    }

    /**
     * Cargar categorías desde el repositorio
     */
    fun cargarCategorias() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                categoriaRepository.obtenerCategorias(usuarioId)
                    .catch { e ->
                        _error.value = "Error al cargar categorías: ${e.message}"
                    }
                    .collect { categorias ->
                        _categorias.value = categorias
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Crear nueva categoría
     */
    fun crearCategoria(
        nombre: String,
        emoji: String,
        color: String,
        tipo: TipoCategoria,
        presupuestado: Double = 0.0
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val nuevaCategoria = Categoria(
                usuarioId = usuarioId,
                nombre = nombre,
                emoji = emoji,
                color = color,
                tipo = tipo,
                presupuestado = presupuestado
            )

            categoriaRepository.crearCategoria(nuevaCategoria)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al crear categoría: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Actualizar categoría existente
     */
    fun actualizarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            _isLoading.value = true

            categoriaRepository.actualizarCategoria(categoria)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al actualizar categoría: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Eliminar categoría
     */
    fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            _isLoading.value = true

            categoriaRepository.desactivarCategoria(categoria.id)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al eliminar categoría: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Filtrar por tipo
     */
    fun filtrarPorTipo(tipo: TipoCategoria?) {
        _tipoFiltro.value = tipo
    }

    /**
     * Limpiar error
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Crear categorías predeterminadas
     */
    fun crearCategoriasDefault() {
        viewModelScope.launch {
            _isLoading.value = true

            categoriaRepository.crearCategoriasDefault(usuarioId)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al crear categorías: ${e.message}"
                }

            _isLoading.value = false
        }
    }
}