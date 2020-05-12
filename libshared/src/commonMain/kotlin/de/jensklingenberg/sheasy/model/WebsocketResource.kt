package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


@Serializable
data class CurrentTurn(val player: Player, val coordinate: Coordinate)


fun ServerRequest.ResetRequest.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerRequest.ResetRequest.serializer(), this)
}

fun ServerRequest.JoinGameRequest.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerRequest.JoinGameRequest.serializer(), this)
}

fun ServerRequest.PlayerRequest.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerRequest.PlayerRequest.serializer(), this)
}

fun ServerRequest.MoveCharRequest.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerRequest.MoveCharRequest.serializer(), this)
}


fun getServerCommandType(toString: String): ServerRequestTypes? {

    return ServerRequestTypes.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}


