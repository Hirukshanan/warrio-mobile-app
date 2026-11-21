package com.warrio.app.ui.product

import com.warrio.app.product.ManualProductValidator

private val monthsWithUnit = Regex("([0-9]+)\\s*months?", RegexOption.IGNORE_CASE)

internal fun receiptProductDraft(text: String): ManualProductUiState {
    val fields = text.lineSequence().mapNotNull { line ->
        val separator = line.indexOf(':')
        if (separator < 0) return@mapNotNull null
        val value = line.substring(separator + 1).trim()
        if (value.isEmpty()) return@mapNotNull null
        line.substring(0, separator).trim().lowercase() to value
    }.toList()

    fun values(vararg labels: String): List<String> = fields
        .filter { (label, _) -> label in labels }
        .map { (_, value) -> value }

    fun field(vararg labels: String): String = values(*labels).distinct().singleOrNull().orEmpty()

    val productNames = values("product", "product name", "item")
    val singleProduct = productNames.size <= 1
    val date = field("purchase date", "date")
    val warranty = if (singleProduct) {
        val explicitMonths = values("warranty months", "warranty duration in months")
        val labelledMonths = values("warranty").mapNotNull { monthsWithUnit.matchEntire(it)?.groupValues?.get(1) }
        (explicitMonths + labelledMonths).distinct().singleOrNull().orEmpty()
    } else {
        ""
    }
    val validation = ManualProductValidator.validate("", "", date, warranty)

    return ManualProductUiState(
        name = productNames.singleOrNull().orEmpty(),
        brand = if (singleProduct) field("brand") else "",
        modelNumber = if (singleProduct) field("model", "model number") else "",
        serialNumber = if (singleProduct) field("serial", "serial number", "s/n") else "",
        purchaseDate = if (validation.purchaseDateError == null) date else "",
        warrantyMonths = if (validation.warrantyMonthsError == null) warranty else "",
    )
}
