package com.lvmh.pocketpet.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.MascotaFirebase
import com.lvmh.pocketpet.datos.local.entidades.MascotaEntity
import com.lvmh.pocketpet.dominio.modelos.Mascota
import com.lvmh.pocketpet.dominio.modelos.TipoMascota

object MascotaMapper {

    // Domain → Firebase
    fun toFirebase(mascota: Mascota): MascotaFirebase {
        return MascotaFirebase(
            id = mascota.id,
            usuarioId = mascota.usuarioId,
            nombre = mascota.nombre,
            emoji = mascota.emoji,
            tipo = mascota.tipo.name,
            nivel = mascota.nivel,
            experiencia = mascota.experiencia,
            salud = mascota.salud,
            felicidad = mascota.felicidad,
            hambre = mascota.hambre,
            energia = mascota.energia,
            monedas = mascota.monedas,
            ultimaAlimentacion = mascota.ultimaAlimentacion,
            ultimaInteraccion = mascota.ultimaInteraccion,
            fechaCreacion = mascota.fechaCreacion
        )
    }

    // Firebase → Domain
    fun fromFirebase(firebase: MascotaFirebase): Mascota {
        return Mascota(
            id = firebase.id,
            usuarioId = firebase.usuarioId,
            nombre = firebase.nombre,
            emoji = firebase.emoji,
            tipo = try {
                TipoMascota.valueOf(firebase.tipo)
            } catch (e: Exception) {
                TipoMascota.PERRO
            },
            nivel = firebase.nivel,
            experiencia = firebase.experiencia,
            salud = firebase.salud,
            felicidad = firebase.felicidad,
            hambre = firebase.hambre,
            energia = firebase.energia,
            monedas = firebase.monedas,
            ultimaAlimentacion = firebase.ultimaAlimentacion,
            ultimaInteraccion = firebase.ultimaInteraccion,
            fechaCreacion = firebase.fechaCreacion
        )
    }

    // Domain → Entity
    fun toEntity(mascota: Mascota): MascotaEntity {
        return MascotaEntity(
            id = mascota.id,
            usuarioId = mascota.usuarioId,
            nombre = mascota.nombre,
            emoji = mascota.emoji,
            tipo = mascota.tipo.name,
            nivel = mascota.nivel,
            experiencia = mascota.experiencia,
            salud = mascota.salud,
            felicidad = mascota.felicidad,
            hambre = mascota.hambre,
            energia = mascota.energia,
            monedas = mascota.monedas,
            ultimaAlimentacion = mascota.ultimaAlimentacion,
            ultimaInteraccion = mascota.ultimaInteraccion,
            fechaCreacion = mascota.fechaCreacion
        )
    }

    // Entity → Domain
    fun fromEntity(entity: MascotaEntity): Mascota {
        return Mascota(
            id = entity.id,
            usuarioId = entity.usuarioId,
            nombre = entity.nombre,
            emoji = entity.emoji,
            tipo = try {
                TipoMascota.valueOf(entity.tipo)
            } catch (e: Exception) {
                TipoMascota.PERRO
            },
            nivel = entity.nivel,
            experiencia = entity.experiencia,
            salud = entity.salud,
            felicidad = entity.felicidad,
            hambre = entity.hambre,
            energia = entity.energia,
            monedas = entity.monedas,
            ultimaAlimentacion = entity.ultimaAlimentacion,
            ultimaInteraccion = entity.ultimaInteraccion,
            fechaCreacion = entity.fechaCreacion
        )
    }
}