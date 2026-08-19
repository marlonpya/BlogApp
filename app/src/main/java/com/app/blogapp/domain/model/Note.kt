package com.app.blogapp.domain.model

/**
 * Nota del usuario. Vive en domain: no conoce Room ni Android.
 * Las fechas son epoch millis para no arrastrar dependencias de formato.
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
