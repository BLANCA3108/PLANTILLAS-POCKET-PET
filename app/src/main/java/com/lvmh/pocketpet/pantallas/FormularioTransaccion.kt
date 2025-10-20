package com.lvmh.pocketpet.pantallas

import com.lvmh.pocketpet.DateBase.Transaccion
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTransaccion(
    onDismiss: () -> Unit,
    onGuardar: (String, String, String, String, String) -> Unit,
    temaColor: Color = Color(0xFF5E35B1)
) {
    val verde = Color(0xFF4CAF50)
    val rojo = Color(0xFFF44336)

    var tipoSeleccionado by remember { mutableStateOf("Ingreso") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Agregar Transacción",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { tipoSeleccionado = "Ingreso" },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipoSeleccionado == "Ingreso") verde else Color.LightGray,
                            contentColor = if (tipoSeleccionado == "Ingreso") Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ingreso", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { tipoSeleccionado = "Gasto" },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipoSeleccionado == "Gasto") rojo else Color.LightGray,
                            contentColor = if (tipoSeleccionado == "Gasto") Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Gasto", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto") },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = Color.Black) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = temaColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoría") },
                    placeholder = { Text("Ej: Comida") },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = Color.Black) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = temaColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fecha") },
                    placeholder = { Text("DD/MM/YYYY") },
                    leadingIcon = { Icon(Icons.Default.DateRange, null, tint = Color.Black) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = temaColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción") },
                    placeholder = { Text("Descripción (opcional)") },
                    leadingIcon = { Icon(Icons.Default.NoteAdd, null, tint = Color.Black) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = temaColor
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E))
                ) {
                    Icon(Icons.Default.PhotoCamera, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar Foto [Preview Imagen]", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (monto.isNotEmpty() && categoria.isNotEmpty() && fecha.isNotEmpty()) {
                            onGuardar(tipoSeleccionado, monto, categoria, fecha, descripcion)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = temaColor)
                ) {
                    Icon(Icons.Default.FolderOpen, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}