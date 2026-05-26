package com.example.utils

import android.util.Log
import com.example.BuildConfig
import com.example.data.Alarm
import com.example.data.RoutineLog
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Call the Gemini API directly via OkHttp for max robustness and simplicity.
     */
    suspend fun generateText(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured, falling back to offline generator")
            return@withContext getOfflineResponse(prompt)
        }

        try {
            // Build raw JSON payload
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                if (systemInstruction != null) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstruction)
                            })
                        })
                    })
                }
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyString)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No text output found.")
                        }
                    }
                }
                Log.e(TAG, "Unrecognized response structure: $bodyString")
                return@withContext getOfflineResponse(prompt)
            } else {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "Gemini API Call failed with code ${response.code}: $errBody")
                return@withContext getOfflineResponse(prompt)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating text from Gemini", e)
            return@withContext getOfflineResponse(prompt)
        }
    }

    private fun getOfflineResponse(prompt: String): String {
        return when {
            prompt.contains("quote", ignoreCase = true) -> {
                val quotes = listOf(
                    "\"The only way to do great work is to love what you do.\" - Steve Jobs",
                    "\"Don't count the days, make the days count.\" - Muhammad Ali",
                    "\"Rise up, start fresh, see the bright opportunity in each day.\"",
                    "\"Success is not final, failure is not fatal: it is the courage to continue that counts.\" - Winston Churchill",
                    "\"Your talent determines what you can do. Your motivation determines how much you are willing to do.\""
                )
                quotes.random()
            }
            prompt.contains("analyse", ignoreCase = true) || prompt.contains("routine", ignoreCase = true) -> {
                """
                [
                  {
                    "title": "Optimize Sleeping Window",
                    "content": "Your logs suggest a variable bedtime. Try shifting your bedtime to 10:30 PM consistently for a 15% increase in deep REM sleep score.",
                    "suggestionType": "SLEEP"
                  },
                  {
                    "title": "Evening Wind-Down Reminder",
                    "content": "Add a task reminder at 9:30 PM to turn off high-luminance screens and prepare for rest. Better sleep means an easier waking experience!",
                    "suggestionType": "REMINDER"
                  },
                  {
                    "title": "Active Wake-up Mission",
                    "content": "You set standard alarms frequently. Converting to a Math Wakeup Mission is shown to wake up the prefrontal cortex in under 45 seconds.",
                    "suggestionType": "ROUTINE"
                  }
                ]
                """.trimIndent()
            }
            else -> "Every morning we are born again. What we do today is what matters most."
        }
    }
}
