package com.lvmh.pocketpet.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.ConfiguracionFirebase
import com.lvmh.pocketpet.datos.local.entidades.ConfiguracionEntity
import com.lvmh.pocketpet.dominio.modelos.Configuracion

// Domain Model -> Firebase DTO
fun Configuracion.toFirebase(): ConfiguracionFirebase {
    return ConfiguracionFirebase(
        id = id,
        userId = userId,
        notificacionesActivas = notificacionesActivas,
        recordatoriosActivos = recordatoriosActivos,
        monedaPredeterminada = monedaPredeterminada,
        recordatoriosCuidadoActivos = recordatoriosCuidadoActivos,
        sonidosActivos = sonidosActivos,
        animacionesActivas = animacionesActivas,
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion
    )
}

// Firebase DTO -> Domain Model
fun ConfiguracionFirebase.toDomain(): Configuracion {
    return Configuracion(
        id = id,
        userId = userId,
        notificacionesActivas = notificacionesActivas,
        recordatoriosActivos = recordatoriosActivos,
        monedaPredeterminada = monedaPredeterminada,
        recordatoriosCuidadoActivos = recordatoriosCuidadoActivos,
        sonidosActivos = sonidosActivos,
        animacionesActivas = animacionesActivas,
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion
    )
}

// Domain Model -> Room Entity
fun Configuracion.toEntity(): ConfiguracionEntity {
    return ConfiguracionEntity(
        id = id,
        userId = userId,
        notificacionesActivas = notificacionesActivas,
        recordatoriosActivos = recordatoriosActivos,
        monedaPredeterminada = monedaPredeterminada,
        recordatoriosCuidadoActivos = recordatoriosCuidadoActivos,
        sonidosActivos = sonidosActivos,
        animacionesActivas = animacionesActivas,
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion
    )
}

// Room Entity -> Domain Model
fun ConfiguracionEntity.toDomain(): Configuracion {
    return Configuracion(
        id = id,
        userId = userId,
        notificacionesActivas = notificacionesActivas,
        recordatoriosActivos = recordatoriosActivos,
        monedaPredeterminada = monedaPredeterminada,
        recordatoriosCuidadoActivos = recordatoriosCuidadoActivos,
        sonidosActivos = sonidosActivos,
        animacionesActivas = animacionesActivas,
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion
    )
}