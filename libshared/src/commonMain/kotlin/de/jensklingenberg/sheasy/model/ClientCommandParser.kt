package de.jensklingenberg.sheasy.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class ClientCommandParser {
    companion object {
        private val json =
            Json(JsonConfiguration.Stable)


        fun getTurnCommand(jsonStr: String): ClientEvent.TurnEvent {
            return json.parse(ClientEvent.TurnEvent.serializer(), jsonStr)
        }

        fun getGameStateChangedCommand(jsonStr: String): ClientEvent.GameStateChanged {
            return json.parse(ClientEvent.GameStateChanged.serializer(), jsonStr)
        }

        fun getPlayerEvent(jsonStr: String): ClientEvent.PlayerEvent {
            return json.parse(ClientEvent.PlayerEvent.serializer(), jsonStr)
        }

        fun getErrorCommand(jsonStr: String): ClientEvent.ErrorEvent {
            return json.parse(ClientEvent.ErrorEvent.serializer(), jsonStr)
        }

        fun toJson(cmd: ClientEvent.ErrorEvent): String {
            return json.stringify(ClientEvent.ErrorEvent.serializer(), cmd)
        }

    }
}