package com.example.brick_hospitalgameapp.network

import com.example.brick_hospitalgameapp.models.UserProfile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

object SupabaseAuth {

    private const val SUPABASE_URL = "https://bxpooqjjozrtxbgbkymf.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ4cG9vcWpqb3pydHhiZ2JreW1mIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTMzNDQ2NDEsImV4cCI6MjA2ODkyMDY0MX0.yUlA7kSOx_02T9LUK3p3znl4BEiEAeqDUbJMuKvbFQ8"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /** 用 username 查 user_profiles */
    suspend fun getUserProfileByUsername(username: String): UserProfile? {
        return try {
            println("🔍 Supabase 查詢 Username: $username")

            val response: HttpResponse = client.get("$SUPABASE_URL/rest/v1/user_profiles") {
                header("apikey", SUPABASE_ANON_KEY)
                header("Authorization", "Bearer $SUPABASE_ANON_KEY")
                url {
                    parameters.append("username", "eq.$username")
                    parameters.append("select", "*")
                }
            }

            // --- 完整 HTTP debug ---
            println("📊 HTTP Status: ${response.status.value}")
            val bodyText = response.bodyAsText()
            println("📄 Response Body: $bodyText")
            // ------------------------

            if (response.status.value !in 200..299) {
                println("❌ HTTP 請求失敗")
                return null
            }

            val jsonElement = try {
                Json.parseToJsonElement(bodyText)
            } catch (e: Exception) {
                println("❌ JSON 解析失敗: ${e.message}")
                e.printStackTrace()
                return null
            }

            val userJson = when(jsonElement) {
                is JsonArray -> jsonElement.firstOrNull()?.jsonObject
                is kotlinx.serialization.json.JsonObject -> jsonElement
                else -> {
                    println("❌ JSON 不是 JsonArray 或 JsonObject")
                    null
                }
            } ?: run {
                println("❌ 沒有找到任何使用者資料")
                return null
            }

            val userProfile = UserProfile(
                id = userJson["id"]?.jsonPrimitive?.content ?: "",
                user_id = userJson["user_id"]?.jsonPrimitive?.content ?: "",
                email = userJson["email"]?.jsonPrimitive?.content ?: "",
                username = userJson["username"]?.jsonPrimitive?.content,
                full_name = userJson["full_name"]?.jsonPrimitive?.content,
                surname = userJson["surname"]?.jsonPrimitive?.content,
                phone = userJson["phone"]?.jsonPrimitive?.content,
                hospital_name = userJson["hospital_name"]?.jsonPrimitive?.content,
                verification_status = userJson["verification_status"]?.jsonPrimitive?.content,
                assigned_doctor = userJson["assigned_doctor"]?.jsonPrimitive?.content
            )

            println("✅ Supabase 回傳 UserProfile: $userProfile")
            return userProfile

        } catch (e: Exception) {
            println("❌ Supabase 查詢異常: ${e.message}")
            e.printStackTrace()
            return null
        }
    }


}
