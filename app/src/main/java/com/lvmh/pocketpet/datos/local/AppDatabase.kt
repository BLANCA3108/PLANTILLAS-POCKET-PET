package com.lvmh.pocketpet.datos.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lvmh.pocketpet.datos.local.dao.*
import com.lvmh.pocketpet.datos.local.entidades.*

@Database(
    entities = [
        UsuarioEntity::class,
        MascotaEntity::class,
        CategoriaEntity::class,
        TransaccionEntity::class,
        PresupuestoEntity::class,
        MetaEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class) // ← AGREGAR ESTO
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun usuarioDao(): UsuarioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun presupuestoDao(): PresupuestoDao
    abstract fun metaDao(): MetaDao

    companion object {
        const val DATABASE_NAME = "pocketpet_database"
    }
}