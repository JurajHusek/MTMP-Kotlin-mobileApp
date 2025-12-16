package com.example.mtmp_kotlin_mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.math.max
import co.yml.charts.common.model.Point as ChartPoint


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
            val y = speed * actualTime * kotlin.math.sin(angleRad) - 0.5 * 9.81 * actualTime * actualTime
            // println("x=$x y=$y")
            points.add(Point(x, y, actualTime))
            actualTime += 0.1f
        }
        val finalX = speed * impactTime * kotlin.math.cos(angleRad)
        points.add(Point(finalX, 0.0, impactTime))
        // println("Napočítaných bodov: ${points.size}")
        // println("Prvý: ${points.first()}")
        // println("Posledný: ${points.last()}")
    } else { 
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:8000/compute?speed=$speed&angle=$angle") // localhost
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val response = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val root = JSONObject(response)
                val arr = root.getJSONArray("points")

                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val x = obj.getDouble("x")
                    val y = obj.getDouble("y")
                    val t = obj.getDouble("time")
                    points.add(Point(x, y, t))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

enum class Screen { Home, Table, Graph, Animation }
@Composable
fun AppRoot() {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }

    when (screen) {
        Screen.Home -> HomeScreen(
            onGoTable = { screen = Screen.Table },
            onGoGraph = { screen = Screen.Graph},
            onGoAnimation = { screen = Screen.Animation},
        )
        Screen.Table -> TableScreen(
            onGoHome = { screen = Screen.Home},
            onGoGraph = { screen = Screen.Graph},
            onGoAnimation = { screen = Screen.Animation}
        )
        Screen.Graph -> GraphScreen(
            onGoHome = { screen = Screen.Home},
            onGoTable = { screen = Screen.Table},
            onGoAnimation = { screen = Screen.Animation}
        )
        Screen.Animation -> AnimationScreen(
            onGoHome = { screen = Screen.Home},
            onGoTable = { screen = Screen.Table},
            onGoGraph = { screen = Screen.Graph}
        )
    }
}

@Preview
@Composable
fun HomeScreen(onGoTable: () -> Unit = {}, onGoGraph: () -> Unit = {}, onGoAnimation: () -> Unit = {}) {
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

            Button(onClick = onGoAnimation, enabled = isComputed) {
                Text("Animation")
            }
        }
    }
}

@Composable
fun TableScreen(
    onGoHome: () -> Unit = {},
    onGoGraph: () -> Unit = {},
    onGoAnimation: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("Table of simulation", style = MaterialTheme.typography.headlineSmall)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 8.dp)
        ) {
            Text("time [s]", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("x [m]", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("y [m]", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(points.size) { i ->
                val p = points[i]
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(String.format(Locale.US, "%.2f", p.time), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(String.format(Locale.US, "%.2f", p.x),    modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(String.format(Locale.US, "%.2f", p.y),    modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = onGoHome, modifier = Modifier.weight(1f)) {
                Text("New simulation")
            }
            Button(onClick = onGoGraph, modifier = Modifier.weight(1f)) {
                Text("Graph")
            }
            Button(onClick = onGoAnimation, modifier = Modifier.weight(1f)) {
                Text("Animation")
            }
        }
    }
}


@Composable
fun GraphScreen(onGoHome: () -> Unit = {}, onGoTable: () -> Unit = {}, onGoAnimation: () -> Unit = {}) {
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

    val line = Line(
        dataPoints = pointsData,
        lineStyle = LineStyle(
            lineType = LineType.Straight()
        )
    )

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(line)
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
            Button(onClick = onGoAnimation) { Text("Animation") }
        }
    }
}

@Composable
fun AnimationScreen(onGoHome: () -> Unit = {}, onGoTable: () -> Unit = {}, onGoGraph: () -> Unit = {}) {
    var i by remember { mutableStateOf(0) }
    var frac by remember { mutableStateOf(0f) }
    LaunchedEffect(points) {
        i = 0
        frac = 0f
        while (i < points.lastIndex) {
            val partLengthMs = 100
            var acc = 0L
            val frame : Long = 16
            while (acc < partLengthMs) {
                delay(frame)
                acc += frame
                frac = (acc.toFloat() / partLengthMs.toFloat()).coerceIn(0f, 1f) 
            }
            i++
            frac = 0f
        }
        i = points.lastIndex - 1 
        frac = 1f
    }

    val maxX = points.maxOf { it.x }
    val maxY = max(points.maxOf { it.y }, 1.0)
    val pad = 24f
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("Animation of simulation", style = MaterialTheme.typography.headlineSmall)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(12.dp)
        ) {
            val left = pad
            val right = size.width - pad
            val bottom = size.height - pad
            val top = pad

            val plotWidth = right - left
            val plotHeight = bottom - top

            // mapovanie suradnic z metrov na pixely
            fun mx(x: Double) = left + (x / maxX * plotWidth).toFloat()
            /* (x / maxX) = pomer (0.0 az 1.0), kde sa nachadza bod voci maxX, * w = natiahne do sirky kresliacej oblasti.
            + left = posunie na offset. */
            fun my(y: Double) = bottom - (y / maxY * plotHeight).toFloat() // invert Y
            // to iste ako mx, ale treba invertovat y nakolko v canvase je y=0 hore

            drawLine(Color.LightGray, Offset(left, my(0.0)), Offset(right, my(0.0)), 2f)
            for (k in 0 until points.lastIndex) {
                val a = points[k];
                val b = points[k + 1]
                drawLine(Color.Gray, Offset(mx(a.x), my(a.y)), Offset(mx(b.x), my(b.y)), 2f)
            }
            val point1 = points[i]
            val point2 = points[i + 1]
            val xNow = point1.x + (point2.x - point1.x) * frac
            val yNow = point1.y + (point2.y - point1.y) * frac

            val canvasX = mx(xNow)
            val canvasY = my(yNow)
            drawCircle(color = Color.Red, radius = 15f, center = Offset(canvasX, canvasY))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = onGoHome) { Text("New simulation") }
            Button(onClick = onGoTable) { Text("Table") }
            Button(onClick = onGoGraph) { Text("Graph") }
        }
    }
}