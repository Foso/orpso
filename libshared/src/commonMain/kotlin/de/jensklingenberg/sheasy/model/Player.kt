package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
data class Player(val id: Int, val symbol: String, val name: String = "Unnamed")

fun Player.toJson(): String {
    val json = Json(JsonConfiguration.Stable)
    val jsonData = json.stringify(Player.serializer(), this)
    return jsonData
}
