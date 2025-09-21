package com.example.mtmp_kotlin_mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import java.util.Locale
import co.yml.charts.common.model.Point as ChartPoint


enum class Screen { Home, Table, Graph }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

data class Point(val x: Double, val y: Double, val time: Double)
val points = mutableListOf<Point>()

fun Compute(speed : Double, angle: Double, online: Boolean) {
    points.clear()
    if(online == false) {
        val angleRad = Math.toRadians(angle)
        val impactTime = 2.0 * speed * kotlin.math.sin(angleRad) / 9.81
        var actualTime: Double = 0.0
        while (actualTime < impactTime) {
            val x = speed * actualTime * kotlin.math.cos(angleRad)
            val y =
                speed * actualTime * kotlin.math.sin(angleRad) - 0.5 * 9.81 * actualTime * actualTime
            println("x=$x y=$y")
            points.add(Point(x, y, actualTime))
            actualTime += 0.5f
        }
        val finalX = speed * impactTime * kotlin.math.cos(angleRad)
        points.add(Point(finalX, 0.0, impactTime))
        println("Napočítaných bodov: ${points.size}")
        println("Prvý: ${points.first()}")
        println("Posledný: ${points.last()}")
    }
}

@Composable
fun AppRoot() {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }

    when (screen) {
        Screen.Home -> HomeScreen(
            onGoTable = { screen = Screen.Table },
            onGoGraph = { screen = Screen.Graph}
        )
        Screen.Table -> TableScreen(
            onGoHome = { screen = Screen.Home},
            onGoGraph = { screen = Screen.Graph}
        )
        Screen.Graph -> GraphScreen(
            onGoHome = { screen = Screen.Home},
            onGoTable = { screen = Screen.Table}
        )
    }
}

@Preview
@Composable
fun HomeScreen(onGoTable: () -> Unit = {}, onGoGraph: () -> Unit = {}) {
    var speedInput = remember { mutableStateOf("") }
    var angleInput = remember { mutableStateOf("") }
    var speed by remember { mutableStateOf(0.0) }
    var angle by remember { mutableStateOf(0.0) }
    var online = remember { mutableStateOf(false) }
    var isComputed by remember { mutableStateOf(false) }

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
        Button(onClick = { Compute(speed, angle, online.value)
            isComputed = true }) {
            Text("Compute")
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = onGoTable, enabled = isComputed) {
                Text("Table")
            }

            Button(onClick = onGoGraph, enabled = isComputed) {
                Text("Graph")
            }

            Button(onClick = { /* TODO */ }) {
                Text("Animation")
            }
        }
    }
}

@Composable
fun TableScreen(onGoHome: () -> Unit = {}, onGoGraph: () -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        Text("Table of simulation", style = MaterialTheme.typography.headlineSmall)
        Row(Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 8.dp)) {
            Text("time [s]", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("x [m]", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("y [m]", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }

        // Scrollovateľná tabuľka
        LazyColumn {
            items(points.size) { i ->
                val p = points[i]
                Row(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Text(String.format(Locale.US, "%.2f", p.time), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(String.format(Locale.US, "%.2f", p.x),    modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(String.format(Locale.US, "%.2f", p.y),    modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = onGoHome) {
                Text("New simulation")
            }

            Button(onClick = onGoGraph) {
                Text("Graph")
            }

            Button(onClick = { /* TODO */ }) {
                Text("Animation")
            }
        }
    }
}

@Composable
fun GraphScreen(onGoHome: () -> Unit = {}, onGoTable: () -> Unit = {}) {

    val pointsData = points.mapIndexed { i, p -> ChartPoint(i.toFloat(), p.y.toFloat()) }
    val stepsX = points.size - 1
    val xAxis = AxisData.Builder()
        .steps(stepsX)
        .labelData { i ->
            if (i in points.indices) String.format("%.2f", points[i].time) else ""
        }
        .build()

    val yMin = points.minOf { it.y }
    val yMax = points.maxOf { it.y }
    val stepsY = 5
    val yAxis = AxisData.Builder()
        .steps(stepsY)
        .labelData { j ->
            val v = yMin + (yMax - yMin) * j / stepsY
            String.format("%.1f", v)
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(Line(pointsData))
        ),
        xAxisData = xAxis,
        yAxisData = yAxis
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("Graph y(time)", style = MaterialTheme.typography.headlineSmall)
        Text("y [m]")
        // graf
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp),
            lineChartData = lineChartData
        )

        Text("time [s]", modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = onGoHome) { Text("New simulation") }
            Button(onClick = onGoTable) { Text("Table") }
            Button(onClick = { /* TODO: Animation */ }) { Text("Animation") }
        }
    }
}
