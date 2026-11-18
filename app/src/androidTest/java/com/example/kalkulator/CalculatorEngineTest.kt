package com.example.kalkulator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun `1_kolejnosc_dzialan`() {
        assertEquals("6", CalculatorEngine.evaluate("2+2*2"))
    }

    @Test
    fun `2_nawiasy`() {
        assertEquals("8", CalculatorEngine.evaluate("(2+2)*2"))
    }

    @Test
    fun `3_dzielenie_przez_zero`() {
        assertEquals("Błąd: dzielenie przez 0", CalculatorEngine.evaluate("1/0"))
    }

    @Test
    fun `4_wyrazenie_niedokonczone`() {
        assertEquals("", CalculatorEngine.evaluate("2+"))
    }

    @Test
    fun `5_nawias_niezamkniety`() {
        assertEquals("", CalculatorEngine.evaluate("((2+2)"))
    }

    @Test
    fun `6_puste_pole`() {
        assertEquals("", CalculatorEngine.evaluate(""))
    }

    @Test
    fun `7_dziedzina_funkcji`() {
        assertEquals("Błąd dziedziny", CalculatorEngine.evaluate("sqrt(-1)"))
    }

    @Test
    fun `8_duze_liczby`() {
        assertEquals("1524157875019052000", CalculatorEngine.evaluate("1234567890*1234567890"))
    }

    @Test
    fun `9_separator_dziesietny_przecinek`() {
        assertEquals("3.5", CalculatorEngine.evaluate("2,5+1"))
    }

    @Test
    fun `10_separator_dziesietny_kropka`() {
        assertEquals("3.5", CalculatorEngine.evaluate("2.5+1"))
    }
}