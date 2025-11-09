package com.example.brick_hospitalgameapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserProfile(
    val id: String,
    val user_id: String,
    val email: String,
    val username: String? = null,
    val full_name: String? = null,
    val surname: String? = null,
    val phone: String? = null,
    val hospital_name: String? = null,
    val assigned_doctor: String? = null,
    val verification_status: String? = null,
    val user_type: String? = null
) : Parcelable
