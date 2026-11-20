package com.warrio.app.ui.document

private const val BYTES_PER_KILOBYTE = 1024L
private const val BYTES_PER_MEGABYTE = BYTES_PER_KILOBYTE * 1024L
internal const val MAX_DOCUMENT_SIZE_BYTES = 10L * BYTES_PER_MEGABYTE

data class DocumentAttachmentUiModel(
    val displayName: String,
    val mimeType: String,
    val sizeBytes: Long?,
    val uri: String,
) {
    val readableDescription: String
        get() = listOf(mimeType.ifBlank { "Unknown type" }, sizeBytes.toReadableFileSize())
            .joinToString(" • ")
}

internal fun validateDocument(document: DocumentAttachmentUiModel): String? = when {
    document.mimeType != "application/pdf" && !document.mimeType.startsWith("image/") ->
        "Choose a PDF or image file."

    document.sizeBytes != null && document.sizeBytes > MAX_DOCUMENT_SIZE_BYTES ->
        "Choose a file smaller than 10 MB."

    else -> null
}

internal fun Long?.toReadableFileSize(): String = when {
    this == null || this < 0L -> "Unknown size"
    this >= BYTES_PER_MEGABYTE -> "%.1f MB".format(this.toDouble() / BYTES_PER_MEGABYTE)
    this >= BYTES_PER_KILOBYTE -> "%.1f KB".format(this.toDouble() / BYTES_PER_KILOBYTE)
    else -> "$this bytes"
}
