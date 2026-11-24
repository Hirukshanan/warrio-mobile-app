package com.warrio.app.ui.claim

import java.util.UUID

enum class WarrantyClaimStatus(val displayName: String) {
    SUBMITTED("Submitted"),
}

data class WarrantyClaimUiModel(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val description: String,
    val status: WarrantyClaimStatus = WarrantyClaimStatus.SUBMITTED,
)

internal fun addClaimToProduct(
    productClaims: MutableMap<String, List<WarrantyClaimUiModel>>,
    claim: WarrantyClaimUiModel,
) {
    productClaims[claim.productId] = productClaims[claim.productId].orEmpty() + claim
}

internal const val MIN_CLAIM_DESCRIPTION_LENGTH = 20
internal const val MAX_CLAIM_DESCRIPTION_LENGTH = 1_000

internal fun validateClaimDescription(description: String): String? = when {
    description.isBlank() -> "Describe the problem with your product."
    description.trim().length < MIN_CLAIM_DESCRIPTION_LENGTH ->
        "Add at least $MIN_CLAIM_DESCRIPTION_LENGTH characters so the problem is clear."

    description.trim().length > MAX_CLAIM_DESCRIPTION_LENGTH ->
        "Keep the description under $MAX_CLAIM_DESCRIPTION_LENGTH characters."

    else -> null
}
