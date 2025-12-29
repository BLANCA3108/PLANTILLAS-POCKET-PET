package com.lvmh.pocketpet.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.UsuarioFirebase
import com.lvmh.pocketpet.datos.local.entidades.UsuarioEntity
import com.lvmh.pocketpet.dominio.modelos.Usuario

object UsuarioMapper {

    // Domain → Firebase
    fun toFirebase(usuario: Usuario): UsuarioFirebase {
        return UsuarioFirebase(
            id = usuario.id,
            nombre = usuario.nombre,
            email = usuario.email,
            avatarUrl = usuario.avatarUrl,
            telefono = usuario.telefono,
            fechaNacimiento = usuario.fechaNacimiento,
            monedaPrincipal = usuario.monedaPrincipal,
            fechaRegistro = usuario.fechaRegistro,
            ultimaActualizacion = usuario.ultimaActualizacion,
            activo = usuario.activo
        )
    }

    // Firebase → Domain
    fun fromFirebase(firebase: UsuarioFirebase): Usuario {
        return Usuario(
            id = firebase.id,
            nombre = firebase.nombre,
            email = firebase.email,
            avatarUrl = firebase.avatarUrl,
            telefono = firebase.telefono,
            fechaNacimiento = firebase.fechaNacimiento,
            monedaPrincipal = firebase.monedaPrincipal,
            fechaRegistro = firebase.fechaRegistro,
            ultimaActualizacion = firebase.ultimaActualizacion,
            activo = firebase.activo
        )
    }

    // Domain → Entity
    fun toEntity(usuario: Usuario): UsuarioEntity {
        return UsuarioEntity(
            id = usuario.id,
            nombre = usuario.nombre,
            email = usuario.email,
            avatarUrl = usuario.avatarUrl,
            telefono = usuario.telefono,
            fechaNacimiento = usuario.fechaNacimiento,
            monedaPrincipal = usuario.monedaPrincipal,
            fechaRegistro = usuario.fechaRegistro,
            ultimaActualizacion = usuario.ultimaActualizacion,
            activo = usuario.activo
        )
    }

    // Entity → Domain
    fun fromEntity(entity: UsuarioEntity): Usuario {
        return Usuario(
            id = entity.id,
            nombre = entity.nombre,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            telefono = entity.telefono,
            fechaNacimiento = entity.fechaNacimiento,
            monedaPrincipal = entity.monedaPrincipal,
            fechaRegistro = entity.fechaRegistro,
            ultimaActualizacion = entity.ultimaActualizacion,
            activo = entity.activo
        )
    }
}