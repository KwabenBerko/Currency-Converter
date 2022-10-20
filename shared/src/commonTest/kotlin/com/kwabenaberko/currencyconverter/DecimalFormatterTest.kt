package com.kwabenaberko.currencyconverter

import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import kotlin.test.Test
import kotlin.test.assertEquals

class DecimalFormatterTest {

    @Test
    fun `should round number to the nearest decimal places`() {
        forAll(
            table(
                headers("number", "decimalPlaces", "expectedNumber"),
                row(17.199, 2, 17.20),
                row(0.0055, 2, 0.01),
                row(0.00020, 2, 0.00),
                row(1.1234, 1, 1.1),
                row(10.101010, 3, 10.101)
            )
        ) { number, decimalPlaces, expectedNumber ->
            val roundedNumber = number.toPlaces(places = decimalPlaces)
            assertEquals(expectedNumber, roundedNumber)
        }
    }

    @Test
    fun `should round number to the nearest significant figures`() {
        forAll(
            table(
                headers("number", "significantFigures", "expectedNumber"),
                row(17.199, 2, 17.00),
                row(0.0055, 2, 0.0055),
                row(0.00020, 2, 0.00020),
            )
        ) { number, decimalPlaces, expectedNumber ->
            val roundedNumber = number.toSignificantDigits(digits = decimalPlaces)
            assertEquals(expectedNumber, roundedNumber)
        }
    }

}
