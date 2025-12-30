package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore

@HiltViewModel
class FirestoreViewModel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel()
