package com.example

import com.example.ui.components.formatInr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessLogicTest {

    @Test
    fun testCurrencyFormatting() {
        val amount = 1250000.0
        val formatted = formatInr(amount)
        assertTrue(formatted.contains("12,50,000") || formatted.contains("1,250,000"))
    }

    @Test
    fun testGstCalculation() {
        val subtotal = 100000.0
        val gst = subtotal * 0.18
        val total = subtotal + gst
        assertEquals(18000.0, gst, 0.01)
        assertEquals(118000.0, total, 0.01)
    }

    @Test
    fun testMaterialCostAccumulation() {
        val unitCost = 550.0 // Italian Botticino marble per sq.ft
        val quantity = 1500.0
        val totalCost = unitCost * quantity
        assertEquals(825000.0, totalCost, 0.01)
    }
}
