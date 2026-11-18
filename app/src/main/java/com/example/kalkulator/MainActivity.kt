package com.example.kalkulator

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kalkulator.ui.theme.CalculatorTheme
import com.example.kalkulator.ui.theme.getButtonColors
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.License
import java.math.BigDecimal

enum class ButtonRole {
    DIGIT,
    OPERATOR,
    ACTION,
    EQUALS,
    SCIENTIFIC
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
        CalcButton(label = ",", role = ButtonRole.DIGIT),
        CalcButton(label = "=", role = ButtonRole.EQUALS),
        CalcButton(label = "+", role = ButtonRole.OPERATOR)
    )
)

val scientificButtons: List<List<CalcButton>> = listOf(
    listOf(
        CalcButton(label = "sin", insertText = "sin(", role = ButtonRole.SCIENTIFIC),
        CalcButton(label = "cos", insertText = "cos(", role = ButtonRole.SCIENTIFIC)
    ),
    listOf(
        CalcButton(label = "tan", insertText = "tan(", role = ButtonRole.SCIENTIFIC),
        CalcButton(label = "π", insertText = "pi", role = ButtonRole.SCIENTIFIC)
    ),
    listOf(
        CalcButton(label = "√", insertText = "sqrt(", role = ButtonRole.SCIENTIFIC),
        CalcButton(label = "xʸ", insertText = "^", role = ButtonRole.SCIENTIFIC)
    ),
    listOf(
        CalcButton(label = "ln", insertText = "ln(", role = ButtonRole.SCIENTIFIC),
        CalcButton(label = "log", insertText = "log10(", role = ButtonRole.SCIENTIFIC)
    ),
    listOf(
        CalcButton(label = "e", insertText = "e", role = ButtonRole.SCIENTIFIC),
        CalcButton(label = "x²", insertText = "^2", role = ButtonRole.SCIENTIFIC)
    )
)

@Composable
fun DisplayField(
    expression: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val textStyle = when {
        expression.length > 35 -> MaterialTheme.typography.headlineSmall
        expression.length > 18 -> MaterialTheme.typography.headlineMedium
        else -> MaterialTheme.typography.displayMedium
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = if (expression.isEmpty()) "0" else expression,
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                softWrap = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            )
        }
    }
}

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
                        shape = RoundedCornerShape(14.dp),
                        colors = getButtonColors(btn.role),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = btn.label,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var expression by rememberSaveable { mutableStateOf("") }

    val onButtonClick: (CalcButton) -> Unit = { btn ->
        when {
            btn.isClear -> expression = ""
            btn.isDelete -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
            btn.role == ButtonRole.EQUALS -> {
                if (expression.isNotBlank()) {
                    val normalized = expression.replace(',', '.')
                    val expr = Expression(normalized)
                    if (expr.checkSyntax()) {
                        val result = expr.calculate()
                        expression = when {
                            result.isNaN() || result.isInfinite() -> {
                                if (expr.errorMessage.contains("Division by zero", ignoreCase = true) || normalized.contains("/0")) {
                                    "Błąd: dzielenie przez 0"
                                } else {
                                    "Błąd dziedziny"
                                }
                            }
                            result == 0.0 -> "0"
                            else -> BigDecimal.valueOf(result).stripTrailingZeros().toPlainString()
                        }
                    }
                }
            }
            else -> expression += btn.insertText
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeDrawingPadding()
            .padding(12.dp)
    ) {
        DisplayField(
            expression = expression,
            modifier = Modifier
                .weight(if (isLandscape) 0.28f else 0.35f)
                .padding(bottom = 10.dp)
        )

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalculatorButtonGrid(
                    rows = scientificButtons,
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxSize(),
                    onButtonClick = onButtonClick
                )
                CalculatorButtonGrid(
                    rows = basicButtons,
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxSize(),
                    onButtonClick = onButtonClick
                )
            }
        } else {
            CalculatorButtonGrid(
                rows = basicButtons,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onButtonClick = onButtonClick
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Deklaracja niekomercyjnego użycia biblioteki mXparser
        License.iConfirmNonCommercialUse("Kalkulator - Projekt Studencki")

        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    CalculatorScreen()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("CALC_STATE", "onSaveInstanceState -> zawartość Bundle:")
        for (key in outState.keySet()) {
            Log.d("CALC_STATE", "Klucz: $key | Wartość: ${outState.get(key)}")
        }
    }
}