package com.lvmh.pocketpet.di

import android.content.Context
import androidx.room.Room
import com.lvmh.pocketpet.datos.local.AppDatabase
import com.lvmh.pocketpet.datos.local.dao.CategoriaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pocket_pet_db"
        ).build()
    }

    @Provides
    fun provideUsuarioDao(db: AppDatabase) = db.usuarioDao()

    @Provides
    fun provideMascotaDao(db: AppDatabase) = db.mascotaDao()

    @Provides
    fun provideCategoriaDao(db: AppDatabase) = db.categoriaDao()

    @Provides
    fun provideTransaccionDao(db: AppDatabase) = db.transaccionDao()

    @Provides
    fun providePresupuestoDao(db: AppDatabase) = db.presupuestoDao()

    @Provides
    fun provideMetaDao(db: AppDatabase) = db.metaDao()
}
