package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration



@Serializable
data class CurrentTurn(val player: Player, val coord: Coord)


fun ServerCommand.ResetCommand.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerCommand.ResetCommand.serializer(), this)
}

fun ServerCommand.JoinGameCommand.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerCommand.JoinGameCommand.serializer(), this)
}

fun ServerCommand.MoveCharCommand.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerCommand.MoveCharCommand.serializer(), this)
}


fun getServerCommandType(toString: String): ServerCommands? {

    return ServerCommands.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}


