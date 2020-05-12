package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


enum class MatchState {
    WIN, LOOSE, DRAW
}

enum class ClientCommands {
     ERROR, TURN, STATE_CHANGED, MESSAGE, PLAYER_EVENT
}

@Serializable
sealed class PlayerResponseEvent {
    @Serializable
    class JOINED(val yourPlayer: Player) : PlayerResponseEvent()
}

@Serializable
sealed class ServerResponse(val id: Int) {

    @Serializable
    class PlayerEvent(val response: PlayerResponseEvent) : ServerResponse(ClientCommands.PLAYER_EVENT.ordinal)

    @Serializable
    class GameStateChanged(val state: GameState) : ServerResponse(ClientCommands.STATE_CHANGED.ordinal)

    @Serializable
    class ErrorEvent(val message: String) : ServerResponse(ClientCommands.ERROR.ordinal)

    @Serializable
    class MessageEvent(val message: String) : ServerResponse(ClientCommands.MESSAGE.ordinal)

    @Serializable
    class TurnEvent(val turn: CurrentTurn, val nextPlayerId: Int) : ServerResponse(ClientCommands.TURN.ordinal)

}

fun ServerResponse.TurnEvent.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerResponse.TurnEvent.serializer(), this)
}

fun ServerResponse.PlayerEvent.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerResponse.PlayerEvent.serializer(), this)
}

fun ServerResponse.GameStateChanged.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerResponse.GameStateChanged.serializer(), this)
}


fun getClientCommandType(toString: String): ClientCommands? {

    return ClientCommands.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}