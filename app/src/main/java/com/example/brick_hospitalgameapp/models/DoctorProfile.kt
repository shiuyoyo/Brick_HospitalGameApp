package com.example.brick_hospitalgameapp.models

import kotlinx.serialization.Serializable

@Serializable
data class DoctorProfile(
    val id: String,
    val user_id: String,
    val license_number: String? = null,
    val specialization: String? = null,
    val years_of_experience: Int? = null,
    val medical_school: String? = null,
    val certifications: List<String>? = null
)