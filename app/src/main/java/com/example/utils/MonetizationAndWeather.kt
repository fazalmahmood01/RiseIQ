package com.example.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WeatherInfo(
    val city: String,
    val temp: Int,
    val condition: String,
    val icon: String // Sun, Rain, Cloud, Snow
)

object WeatherClient {
    private val _weather = MutableStateFlow(WeatherInfo("New York", 72, "Sunny", "☀️"))
    val weather: StateFlow<WeatherInfo> = _weather

    fun updateCity(city: String) {
        val hash = city.lowercase().hashCode()
        val temp = (hash % 15) + (if (hash % 2 == 0) 65 else 45) // range between 30 and 80 F
        val conditions = listOf("Sunny", "Cloudy", "Drizzle", "Light Rain", "Breezy", "Hazy")
        val icons = listOf("☀️", "☁️", "🌧️", "🌦️", "💨", "🌫️")
        val index = kotlin.math.abs(hash) % conditions.size
        _weather.value = WeatherInfo(
            city.capitalize(),
            temp,
            conditions[index],
            icons[index]
        )
    }
}

object PremiumManager {
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    fun setPremium(premium: Boolean) {
        _isPremium.value = premium
    }
}
