package com.warrio.app.product

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

data class ManualProductValidation(
    val nameError: String? = null,
    val brandError: String? = null,
    val purchaseDateError: String? = null,
    val warrantyMonthsError: String? = null,
) {
    val isValid: Boolean
        get() = nameError == null &&
            brandError == null &&
            purchaseDateError == null &&
            warrantyMonthsError == null
}

object ManualProductValidator {
    fun validate(
        name: String,
        brand: String,
        purchaseDate: String,
        warrantyMonths: String,
    ): ManualProductValidation {
        return ManualProductValidation(
            nameError = if (name.isBlank()) "Product name is required." else null,
            brandError = if (brand.isBlank()) "Brand is required." else null,
            purchaseDateError = when {
                purchaseDate.isBlank() -> "Purchase date is required."
                !isValidIsoDate(purchaseDate) -> "Use a valid date in YYYY-MM-DD format."
                else -> null
            },
            warrantyMonthsError = validateWarrantyMonths(warrantyMonths),
        )
    }

    private fun validateWarrantyMonths(value: String): String? {
        if (value.isBlank()) return "Warranty duration is required."
        val months = value.toIntOrNull()
            ?: return "Warranty duration must be a whole number."
        return if (months in 1..120) {
            null
        } else {
            "Warranty duration must be between 1 and 120 months."
        }
    }

    private fun isValidIsoDate(value: String): Boolean {
        if (!value.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return false
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
            isLenient = false
        }
        val position = ParsePosition(0)
        return formatter.parse(value, position) != null && position.index == value.length
    }
}
