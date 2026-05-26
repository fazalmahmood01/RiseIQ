package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.US
        } else {
            Log.e("TTSManager", "Initialization failed")
        }
    }

    fun speak(text: String, languageCode: String) {
        if (!isInitialized) {
            Log.w("TTSManager", "TTS is not initialized yet")
            return
        }

        val locale = when (languageCode) {
            "HIN" -> Locale("hi", "IN")
            "URD" -> Locale("ur", "PK")
            else -> Locale.US
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("TTSManager", "Language $languageCode is not supported, defaulting to English")
            tts?.language = Locale.US
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RiseIQTTS")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
