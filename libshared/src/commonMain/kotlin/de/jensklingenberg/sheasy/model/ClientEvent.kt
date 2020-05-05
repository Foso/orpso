package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


enum class ClientCommands {
    JOINED,  ERROR,  TURN, STATE_CHANGED, MESSAGE
}

@Serializable
sealed class ClientEvent(val id: Int) {

    @Serializable
    class GameJoined(val yourPlayer: Player) : ClientEvent(ClientCommands.JOINED.ordinal)

    @Serializable
    class GameStateChanged(val state: GameState) : ClientEvent(ClientCommands.STATE_CHANGED.ordinal)

    @Serializable
    class ErrorEvent(val message: String) : ClientEvent(ClientCommands.ERROR.ordinal)

    @Serializable
    class MessageEvent(val message: String) : ClientEvent(ClientCommands.MESSAGE.ordinal)

    @Serializable
    class TurnEvent(val turn: CurrentTurn, val nextPlayerId: Int) : ClientEvent(ClientCommands.TURN.ordinal)
}

fun ClientEvent.TurnEvent.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.TurnEvent.serializer(), this)
}

fun ClientEvent.GameJoined.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.GameJoined.serializer(), this)
}

fun ClientEvent.GameStateChanged.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.GameStateChanged.serializer(), this)
}

class ClientCommandParser {
    companion object {
        private val json = Json(JsonConfiguration.Stable)
        fun getGameJoinedCommand(jsonStr: String): ClientEvent.GameJoined {
            return json.parse(ClientEvent.GameJoined.serializer(), jsonStr)
        }

        fun getTurnCommand(jsonStr: String): ClientEvent.TurnEvent {
            return json.parse(ClientEvent.TurnEvent.serializer(), jsonStr)
        }

        fun getGameStateChangedCommand(jsonStr: String): ClientEvent.GameStateChanged {
            return json.parse(ClientEvent.GameStateChanged.serializer(), jsonStr)
        }

        fun getErrorCommand(jsonStr: String): ClientEvent.ErrorEvent {
            return json.parse(ClientEvent.ErrorEvent.serializer(), jsonStr)
        }

        fun toJson(cmd: ClientEvent.ErrorEvent): String {
            return json.stringify(ClientEvent.ErrorEvent.serializer(), cmd)
        }

    }
}


fun getClientCommandType(toString: String): ClientCommands? {

    return ClientCommands.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}