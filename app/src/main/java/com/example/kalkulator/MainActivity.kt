package com.example.kalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// --- MODEL DANYCH PRZYCISKÓW ---
enum class ButtonRole {
    DIGIT,
    OPERATOR,
    ACTION,
    EQUALS
}

data class CalcButton(
    val label: String,
    val insertText: String = label,
    val role: ButtonRole = ButtonRole.DIGIT,
    val isClear: Boolean = false,
    val isDelete: Boolean = false
)

val basicButtons: List<List<CalcButton>> = listOf(
    listOf(
        CalcButton(label = "C", role = ButtonRole.ACTION, isClear = true),
        CalcButton(label = "⌫", role = ButtonRole.ACTION, isDelete = true),
        CalcButton(label = "(", role = ButtonRole.ACTION),
        CalcButton(label = ")", role = ButtonRole.ACTION)
    ),
    listOf(
        CalcButton(label = "7", role = ButtonRole.DIGIT),
        CalcButton(label = "8", role = ButtonRole.DIGIT),
        CalcButton(label = "9", role = ButtonRole.DIGIT),
        CalcButton(label = "÷", insertText = "/", role = ButtonRole.OPERATOR)
    ),
    listOf(
        CalcButton(label = "4", role = ButtonRole.DIGIT),
        CalcButton(label = "5", role = ButtonRole.DIGIT),
        CalcButton(label = "6", role = ButtonRole.DIGIT),
        CalcButton(label = "×", insertText = "*", role = ButtonRole.OPERATOR)
    ),
    listOf(
        CalcButton(label = "1", role = ButtonRole.DIGIT),
        CalcButton(label = "2", role = ButtonRole.DIGIT),
        CalcButton(label = "3", role = ButtonRole.DIGIT),
        CalcButton(label = "-", role = ButtonRole.OPERATOR)
    ),
    listOf(
        CalcButton(label = "0", role = ButtonRole.DIGIT),
        CalcButton(label = ".", role = ButtonRole.DIGIT),
        CalcButton(label = "=", role = ButtonRole.EQUALS),
        CalcButton(label = "+", role = ButtonRole.OPERATOR)
    )
)

// --- KOMPONENTY EKRANU ---

// Pole działania z przewijaniem horyzontalnym (długie wyrażenie nie znika za krawędzią)
@Composable
fun DisplayField(
    expression: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            text = if (expression.isEmpty()) "0" else expression,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.horizontalScroll(scrollState)
        )
    }
}

// Skalowalna siatka przycisków wykorzystująca Modifier.weight
@Composable
fun CalculatorButtonGrid(
    rows: List<List<CalcButton>>,
    modifier: Modifier = Modifier,
    onButtonClick: (CalcButton) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowButtons ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowButtons.forEach { btn ->
                    Button(
                        onClick = { onButtonClick(btn) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = btn.label,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}

// Główny ekran kalkulatora
@Composable
fun CalculatorScreen() {
    var expression by rememberSaveable { mutableStateOf("") }

    val onButtonClick: (CalcButton) -> Unit = { btn ->
        when {
            btn.isClear -> expression = ""
            btn.isDelete -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
            else -> expression += btn.insertText
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DisplayField(
            expression = expression,
            modifier = Modifier.weight(0.4f)
        )
        CalculatorButtonGrid(
            rows = basicButtons,
            modifier = Modifier.weight(1f),
            onButtonClick = onButtonClick
        )
    }
}

// --- AKTYWNOŚĆ ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                CalculatorScreen()
            }
        }
    }
}