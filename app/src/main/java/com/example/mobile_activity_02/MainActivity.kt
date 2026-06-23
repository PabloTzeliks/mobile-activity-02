package com.example.mobile_activity_02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_activity_02.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

// ─── Data ────────────────────────────────────────────────────────────────────

data class FreightResult(
    val region: String,
    val percentual: Double,
    val valorFrete: Double,
    val valorTotal: Double
)

val REGIONS = listOf("Sul", "Sudeste", "Centro-Oeste", "Nordeste", "Norte")

fun freightPercentual(region: String): Double = when (region.uppercase(Locale.ROOT)) {
    "SUL"          -> 0.05
    "SUDESTE"      -> 0.07
    "CENTRO-OESTE" -> 0.08
    "NORDESTE"     -> 0.10
    "NORTE"        -> 0.12
    else           -> 0.0
}

// ─── Activity ─────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobileactivity02Theme {
                FreightCalculatorApp()
            }
        }
    }
}

// ─── Root Screen ──────────────────────────────────────────────────────────────

@Composable
fun FreightCalculatorApp() {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Navy900, Navy800)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))
            AppHeader()
            Spacer(Modifier.height(28.dp))
            FreightCalculatorCard()
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
fun AppHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(Teal500, Navy700))
                )
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Calculadora de Frete",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Logística Industrial · Brasil",
            color = Teal400,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.2.sp
        )
    }
}

// ─── Main Card ────────────────────────────────────────────────────────────────

@Composable
fun FreightCalculatorCard() {
    var selectedRegion by remember { mutableStateOf("") }
    var valorText      by remember { mutableStateOf("") }
    var result         by remember { mutableStateOf<FreightResult?>(null) }
    var errorMsg       by remember { mutableStateOf("") }

    fun calcular() {
        errorMsg = ""
        if (selectedRegion.isBlank()) {
            errorMsg = "Selecione uma região de destino."
            return
        }
        val valor = valorText.replace(",", ".").toDoubleOrNull()
        if (valor == null || valor <= 0.0) {
            errorMsg = "Informe um valor válido para a mercadoria."
            return
        }
        val pct        = freightPercentual(selectedRegion)
        val valorFrete = valor * pct
        val valorTotal = valor + valorFrete
        result = FreightResult(selectedRegion, pct * 100, valorFrete, valorTotal)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(24.dp)),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Section label
            SectionLabel("Dados da Entrega")

            // ── Region selector
            RegionSelector(
                selected   = selectedRegion,
                onSelected = { selectedRegion = it; result = null; errorMsg = "" }
            )

            // ── Value input
            ValueInput(
                value    = valorText,
                onChange = { valorText = it; result = null; errorMsg = "" }
            )

            // ── Error
            if (errorMsg.isNotBlank()) {
                Text(
                    text  = "⚠ $errorMsg",
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // ── Button
            Button(
                onClick  = { calcular() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal500)
            ) {
                Text(
                    text       = "Calcular Frete",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Navy900
                )
            }

            // ── Results
            AnimatedVisibility(
                visible = result != null,
                enter   = fadeIn(tween(400)) + slideInVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    initialOffsetY = { it / 2 }
                )
            ) {
                result?.let { ResultPanel(it) }
            }
        }
    }
}

// ─── Section label ────────────────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String) {
    Text(
        text       = text.uppercase(Locale.ROOT),
        color      = Teal400,
        fontSize   = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp
    )
}

// ─── Region Selector ──────────────────────────────────────────────────────────

@Composable
fun RegionSelector(selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text       = "Região de Destino",
            color      = Color.White,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        // Two rows: 3 + 2
        val rows = listOf(
            REGIONS.take(3),
            REGIONS.drop(3)
        )
        rows.forEach { row ->
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { region ->
                    val isSelected = region == selected
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) Teal500 else Navy700
                            )
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = Navy700,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelected(region) }
                    ) {
                        Text(
                            text       = region,
                            color      = if (isSelected) Navy900 else Teal400,
                            fontSize   = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign  = TextAlign.Center
                        )
                    }
                }
                // fill empty slots in last row
                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ─── Value Input ──────────────────────────────────────────────────────────────

@Composable
fun ValueInput(value: String, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text       = "Valor da Mercadoria (R$)",
            color      = Color.White,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value         = value,
            onValueChange = onChange,
            placeholder   = { Text("Ex: 1500,00", color = Color(0xFF8899AA)) },
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape  = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Teal500,
                unfocusedBorderColor = Color(0xFF334455),
                focusedTextColor     = Color.White,
                unfocusedTextColor   = Color.White,
                cursorColor          = Teal400,
                focusedContainerColor   = Navy700,
                unfocusedContainerColor = Navy700
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─── Result Panel ─────────────────────────────────────────────────────────────

@Composable
fun ResultPanel(result: FreightResult) {
    val brl = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = Color(0xFF243447), thickness = 1.dp)
        SectionLabel("Resultado do Cálculo")

        // Freight table card header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Navy700)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Green400, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Região: ${result.region}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Metric cards
        ResultMetricCard(
            label = "Percentual de Frete",
            value = "${"%.0f".format(result.percentual)}%",
            accent = Amber400
        )
        ResultMetricCard(
            label = "Valor do Frete",
            value = brl.format(result.valorFrete),
            accent = Teal400
        )
        ResultMetricCard(
            label  = "Valor Total (c/ Frete)",
            value  = brl.format(result.valorTotal),
            accent = Green400,
            isBig  = true
        )
    }
}

@Composable
fun ResultMetricCard(label: String, value: String, accent: Color, isBig: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Navy700)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 18.dp, vertical = if (isBig) 18.dp else 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            color      = Color(0xFFAABBCC),
            fontSize   = if (isBig) 14.sp else 13.sp,
            fontWeight = if (isBig) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text       = value,
            color      = accent,
            fontSize   = if (isBig) 20.sp else 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}