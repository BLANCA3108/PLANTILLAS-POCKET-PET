package com.lvmh.pocketpet.di

import android.content.Context
import androidx.room.Room
import com.lvmh.pocketpet.datos.local.AppDatabase
import com.lvmh.pocketpet.datos.local.dao.*
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
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUsuarioDao(db: AppDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    @Singleton
    fun provideMascotaDao(db: AppDatabase): MascotaDao = db.mascotaDao()

    @Provides
    @Singleton
    fun provideCategoriaDao(db: AppDatabase): CategoriaDao = db.categoriaDao()

    @Provides
    @Singleton
    fun provideTransaccionDao(db: AppDatabase): TransaccionDao = db.transaccionDao()

    @Provides
    @Singleton
    fun providePresupuestoDao(db: AppDatabase): PresupuestoDao = db.presupuestoDao()

    @Provides
    @Singleton
    fun provideMetaDao(db: AppDatabase): MetaDao = db.metaDao()
}