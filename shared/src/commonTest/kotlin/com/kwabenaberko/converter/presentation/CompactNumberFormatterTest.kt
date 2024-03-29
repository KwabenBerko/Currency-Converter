package com.kwabenaberko.converter.presentation

import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import kotlin.test.Test
import kotlin.test.assertEquals

class CompactNumberFormatterTest {
    private val sut = CompactNumberFormatter()

    @Test
    fun `should return number as string without formatting if number is less than thousand`() {
        forAll(
            table(
                headers("rawNumber", "formattedNumber"),
                row(0.0, "0"),
                row(0.00026, "0.00026"),
                row(0.022759, "0.022759"),
                row(0.000473, "0.000473"),
                row(0.000418, "0.000418"),
                row(0.000099, "0.000099"),
                row(20.0, "20"),
                row(500.0, "500"),
                row(999.9, "999.9"),
            )
        ) { rawNumber, formattedNumber ->
            val result = sut.format(rawNumber)
            assertEquals(formattedNumber, result)
        }
    }

    @Test
    fun `should compact to K if number is greater than or equal to a thousand but less than a million`() {
        forAll(
            table(
                headers("rawNumber", "formattedNumber"),
                row(1_000.0, "1K"),
                row(20_000.0, "20K"),
                row(500_000.0, "500K"),
                row(999_999.9, "999.9K"),
            )
        ) { rawNumber, formattedNumber ->
            val result = sut.format(rawNumber)
            assertEquals(formattedNumber, result)
        }
    }

    @Test
    fun `should compact to M if number is greater than or equal to a million but less than a billion`() {
        forAll(
            table(
                headers("rawNumber", "formattedNumber"),
                row(1_000_000.0, "1M"),
                row(20_000_000.0, "20M"),
                row(500_000_000.0, "500M"),
                row(999_999_999.9, "999.9M"),
            )
        ) { rawNumber, formattedNumber ->
            val result = sut.format(rawNumber)
            assertEquals(formattedNumber, result)
        }
    }

    @Test
    fun `should compact to B if number is greater than or equal to a billion but less than a trillion`() {
        forAll(
            table(
                headers("rawNumber", "formattedNumber"),
                row(1_000_000_000.0, "1B"),
                row(20_000_000_000.0, "20B"),
                row(500_000_000_000.0, "500B"),
                row(999_999_999_999.9, "999.9B"),
            )
        ) { rawNumber, formattedNumber ->
            val result = sut.format(rawNumber)
            assertEquals(formattedNumber, result)
        }
    }

    @Test
    fun `should compact to T if number is greater than or equal to a trillion`() {
        forAll(
            table(
                headers("rawNumber", "formattedNumber"),
                row(1_000_000_000_000.0, "1T"),
                row(20_000_000_000_000.0, "20T"),
                row(500_000_000_000_000.0, "500T"),
                row(999_999_999_999_999.9, "999.9T"),
            )
        ) { rawNumber, formattedNumber ->
            val result = sut.format(rawNumber)
            assertEquals(formattedNumber, result)
        }
    }
}
