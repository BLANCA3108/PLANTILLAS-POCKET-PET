package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.lvmh.pocketpet.datos.local.AppDatabase

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    val database: AppDatabase
) : ViewModel()
