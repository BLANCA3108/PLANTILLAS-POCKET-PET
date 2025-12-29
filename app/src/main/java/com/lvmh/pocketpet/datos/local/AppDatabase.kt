package com.lvmh.pocketpet.datos.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lvmh.pocketpet.datos.local.dao.CategoriaDao
import com.lvmh.pocketpet.datos.local.dao.MascotaDao
import com.lvmh.pocketpet.datos.local.dao.TransaccionDao
import com.lvmh.pocketpet.datos.local.dao.UsuarioDao
import com.lvmh.pocketpet.datos.local.entidades.CategoriaEntity
import com.lvmh.pocketpet.datos.local.entidades.MascotaEntity
import com.lvmh.pocketpet.datos.local.entidades.PresupuestoEntity
import com.lvmh.pocketpet.datos.local.entidades.TransaccionEntity
import com.lvmh.pocketpet.datos.local.entidades.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        MascotaEntity::class,
        CategoriaEntity::class,
        TransaccionEntity::class,
        PresupuestoEntity::class
        // Agrega aquí MetaAhorroEntity si la tienes
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun usuarioDao(): UsuarioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun presupuestoDao(): PresupuestoDao
    // Agrega aquí metaAhorroDao() si lo tienes

    companion object {
        const val DATABASE_NAME = "pocketpet_database"
    }
}