package ru.oiltechclean.assistant.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * Отправка пожеланий на настроенный HTTPS-приёмник.
 *
 * Формат запроса совпадает с iOS-версией приложения:
 * `{ type, message, contact, app, platform, appVersion, sentAt }`.
 */
object FeedbackSender {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun send(
        message: String,
        contact: String,
        appVersion: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (!FeedbackConfig.isConfigured) {
            return@withContext Result.failure(
                IllegalStateException("Форма обратной связи ещё не подключена к приёмнику.")
            )
        }

        val payload = JSONObject().apply {
            put("type", "feedback")
            put("message", message)
            put("contact", contact.ifBlank { "—" })
            put("app", "OilTech Assistant")
            put("platform", "Android")
            put("appVersion", appVersion.ifBlank { "unknown" })
            put("sentAt", Instant.now().toString())
        }

        val request = Request.Builder()
            .url(FeedbackConfig.ENDPOINT)
            .header("Accept", "application/json")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            val statusCode = client.newCall(request).execute().use { it.code }
            if (statusCode !in 200..299) {
                Result.failure(
                    IllegalStateException("Сервер отклонил сообщение (код $statusCode). Попробуйте позже.")
                )
            } else {
                Result.success(Unit)
            }
        } catch (io: IOException) {
            Result.failure(
                IOException("Не удалось отправить сообщение. Проверьте подключение к интернету.", io)
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
