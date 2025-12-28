package com.lvmh.pocketpet.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.CategoriaFirebase
import com.lvmh.pocketpet.datos.local.entidades.CategoriaEntity
import com.lvmh.pocketpet.dominio.modelos.Categoria
import com.lvmh.pocketpet.dominio.modelos.TipoCategoria

object CategoriaMapper {

    // Domain → Firebase
    fun toFirebase(categoria: Categoria): CategoriaFirebase {
        return CategoriaFirebase(
            id = categoria.id,
            usuarioId = categoria.usuarioId,
            nombre = categoria.nombre,
            emoji = categoria.emoji,
            color = categoria.color,
            tipo = categoria.tipo.name,
            presupuestado = categoria.presupuestado,
            gastado = categoria.gastado,
            activa = categoria.activa,
            fechaCreacion = categoria.fechaCreacion
        )
    }

    // Firebase → Domain
    fun fromFirebase(firebase: CategoriaFirebase): Categoria {
        return Categoria(
            id = firebase.id,
            usuarioId = firebase.usuarioId,
            nombre = firebase.nombre,
            emoji = firebase.emoji,
            color = firebase.color,
            tipo = try {
                TipoCategoria.valueOf(firebase.tipo)
            } catch (e: Exception) {
                TipoCategoria.GASTO
            },
            presupuestado = firebase.presupuestado,
            gastado = firebase.gastado,
            activa = firebase.activa,
            fechaCreacion = firebase.fechaCreacion
        )
    }

    // Domain → Entity (Room)
    fun toEntity(categoria: Categoria): CategoriaEntity {
        return CategoriaEntity(
            id = categoria.id,
            usuarioId = categoria.usuarioId,
            nombre = categoria.nombre,
            emoji = categoria.emoji,
            color = categoria.color,
            tipo = categoria.tipo.name,
            presupuestado = categoria.presupuestado,
            gastado = categoria.gastado,
            activa = categoria.activa,
            fechaCreacion = categoria.fechaCreacion
        )
    }

    // Entity (Room) → Domain
    fun fromEntity(entity: CategoriaEntity): Categoria {
        return Categoria(
            id = entity.id,
            usuarioId = entity.usuarioId,
            nombre = entity.nombre,
            emoji = entity.emoji,
            color = entity.color,
            tipo = try {
                TipoCategoria.valueOf(entity.tipo)
            } catch (e: Exception) {
                TipoCategoria.GASTO
            },
            presupuestado = entity.presupuestado,
            gastado = entity.gastado,
            activa = entity.activa,
            fechaCreacion = entity.fechaCreacion
        )
    }

    // Listas
    fun fromFirebaseList(list: List<CategoriaFirebase>): List<Categoria> {
        return list.map { fromFirebase(it) }
    }

    fun fromEntityList(list: List<CategoriaEntity>): List<Categoria> {
        return list.map { fromEntity(it) }
    }

    fun toEntityList(list: List<Categoria>): List<CategoriaEntity> {
        return list.map { toEntity(it) }
    }
}