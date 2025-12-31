package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lvmh.pocketpet.dominio.modelos.Categoria
import com.lvmh.pocketpet.dominio.modelos.TipoCategoria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class EstadoCategorias(
    val categorias: List<Categoria> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoCategorias())
    val estado: StateFlow<EstadoCategorias> = _estado.asStateFlow()

    private var categoriasListener: ListenerRegistration? = null
    private var usuarioId: String? = null

    init {
        inicializar()
    }

    fun inicializar() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            usuarioId = userId
            escucharCategorias(userId)
        }
    }

    private fun escucharCategorias(userId: String) {
        categoriasListener?.remove()
        _estado.value = _estado.value.copy(cargando = true)

        println("🔵 [CategoriaVM] Escuchando categorías del usuario: $userId")

        categoriasListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("categorias")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ [CategoriaVM] Error: ${error.message}")
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = error.message
                    )
                    return@addSnapshotListener
                }

                val categorias = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Categoria(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            emoji = doc.getString("emoji") ?: "📊",
                            tipo = TipoCategoria.valueOf(doc.getString("tipo") ?: "GASTO"),
                            presupuestado = doc.getDouble("presupuestado") ?: 0.0
                        )
                    } catch (e: Exception) {
                        println("❌ [CategoriaVM] Error parseando: ${e.message}")
                        null
                    }
                } ?: emptyList()

                println("✅ [CategoriaVM] Categorías cargadas: ${categorias.size}")
                categorias.forEach {
                    println("   - ${it.emoji} ${it.nombre} (${it.tipo})")
                }

                _estado.value = _estado.value.copy(
                    categorias = categorias,
                    cargando = false,
                    error = null
                )
            }
    }

    // 🔥 FUNCIÓN PRINCIPAL: Crear categoría desde cualquier lugar
    fun crearCategoria(
        nombre: String,
        emoji: String,
        tipo: TipoCategoria,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val userId = usuarioId ?: run {
            onError("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            try {
                println("🔵 [CategoriaVM] Creando categoría: $nombre ($emoji) - $tipo")

                val categoriaData = hashMapOf(
                    "nombre" to nombre,
                    "emoji" to emoji,
                    "tipo" to tipo.name,
                    "presupuestado" to 0.0,
                    "fecha_creacion" to System.currentTimeMillis(),
                    "usuario_id" to userId
                )

                val docRef = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("categorias")
                    .add(categoriaData)
                    .await()

                println("✅ [CategoriaVM] Categoría creada: ${docRef.id}")
                onSuccess(docRef.id)

            } catch (e: Exception) {
                println("❌ [CategoriaVM] Error creando: ${e.message}")
                onError("Error al crear categoría: ${e.message}")
                _estado.value = _estado.value.copy(
                    error = "Error al crear categoría: ${e.message}"
                )
            }
        }
    }

    fun obtenerCategoriasPorTipo(tipo: TipoCategoria): List<Categoria> {
        return _estado.value.categorias.filter { it.tipo == tipo }
    }

    fun limpiarError() {
        _estado.value = _estado.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        categoriasListener?.remove()
    }
}