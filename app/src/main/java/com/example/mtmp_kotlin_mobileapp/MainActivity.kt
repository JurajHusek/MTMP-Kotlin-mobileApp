package com.example.mtmp_kotlin_mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreen() {
    var speedInput = remember { mutableStateOf("") }
    var angleInput = remember { mutableStateOf("") }
    var speed : Double = 0.0
    var angle : Double = 0.0
    var online = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text("Parabolic throwing simulator", style = MaterialTheme.typography.headlineSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Server-side computing"
            )
            Checkbox(
                checked = online.value,
                onCheckedChange = { online.value = it }
            )
        }


        Text("Set start values", style = MaterialTheme.typography.labelSmall)
        OutlinedTextField(
            value = speedInput.value,
            onValueChange = {
                speedInput.value = it
                speed = speedInput.value.toDoubleOrNull() ?: 0.0 },
            label = { Text("Speed (m/s)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = angleInput.value,
            onValueChange = { angleInput.value = it
                angle = angleInput.value.toDoubleOrNull() ?: 0.0 },
            label = { Text("Angle (degrees)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = { /* TODO */ }) {
                Text("Table")
            }

            Button(onClick = { /* TODO */ }) {
                Text("Graph")
            }

            Button(onClick = { /* TODO */ }) {
                Text("Animation")
            }
        }
    }
}
