package com.example.kalkulator

import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.License
import java.math.BigDecimal

object CalculatorEngine {

    init {
        License.iConfirmNonCommercialUse("Kalkulator - Projekt Studencki")
    }

    fun evaluate(expression: String): String {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return ""
        }

        // Zamiana polskiego przecinka dziesiętnego na kropkę wymaganą przez bibliotekę
        val normalized = trimmed.replace(',', '.')

        // Wyrażenie zakończone operatorem lub otwartym nawiasem jest niedokończone
        if (normalized.last() in charArrayOf('+', '-', '*', '/', '^', '(', '.')) {
            return ""
        }

        val expr = Expression(normalized)

        // Odrzucenie błędów składniowych (np. niedomkniętych nawiasów "((2+2)")
        if (!expr.checkSyntax()) {
            return ""
        }

        val result = expr.calculate()

        return when {
            result.isNaN() || result.isInfinite() -> {
                when {
                    expr.errorMessage.contains("Division by zero", ignoreCase = true) ||
                            normalized.contains("/0") -> "Błąd: dzielenie przez 0"
                    normalized.contains("sqrt(-") ||
                            normalized.contains("ln(-") ||
                            normalized.contains("ln(0)") ||
                            normalized.contains("log10(-") ||
                            normalized.contains("log10(0)") -> "Błąd dziedziny"
                    else -> ""
                }
            }
            result == 0.0 -> "0"
            else -> BigDecimal.valueOf(result).stripTrailingZeros().toPlainString()
        }
    }
}