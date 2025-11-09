package com.example.brick_hospitalgameapp.models

import kotlinx.serialization.Serializable

@Serializable
data class HealthRecord(
    val id: String,
    val user_id: String,
    val age: Int? = null,
    val gender: String? = null,
    val birth_date: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val stroke_level: String? = null,
    val disability_level: String? = null,
    val medical_conditions: List<String>? = null,
    val medications: List<String>? = null,
    val emergency_contact_name: String? = null,
    val emergency_contact_phone: String? = null
)