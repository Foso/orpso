package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

data class WebsocketResource<T>(val type: WebSocketType, val data: T?, val message: String = "") {

}

enum class WebSocketType {
    Notification,

    MESSAGE,
    EVENT,
    UNKNOWN
}


@Serializable
data class Player(val id: Int,val symbol:String, val name : String= "Unnamed")

fun Player.toJson(): String {
    val json = Json(JsonConfiguration.Stable)
    val jsonData = json.stringify(Player.serializer(), this)
    return jsonData
}

enum class ClientCommands {
    JOINED, GENERAL, MAKEMOVE, ERROR, UNKNOWN, GAME_ENDED, NEW_GAME, TURN
}

@Serializable
sealed class ClientEvent(val id: Int) {

    @Serializable
    class GameJoined(val player: Player) : ClientEvent(ClientCommands.JOINED.ordinal)

    @Serializable
    class NewGame() : ClientEvent(ClientCommands.NEW_GAME.ordinal)

    @Serializable
    class GameEnded(val winnerId: Int) : ClientEvent(ClientCommands.GAME_ENDED.ordinal)

    @Serializable
    class GeneralEvent(val cmdID: Int, val json: String) : ClientEvent(ClientCommands.GENERAL.ordinal)

    @Serializable
    class MakeMoveEvent(val playerId: Int, val coord: Coord) : ClientEvent(ClientCommands.MAKEMOVE.ordinal)

    @Serializable
    class ErrorEvent(val message: String) : ClientEvent(ClientCommands.ERROR.ordinal)

    @Serializable
    class TurnEvent(val turn: CurrentTurn,val nextPlayerId:Int) : ClientEvent(ClientCommands.TURN.ordinal)
}

@Serializable
data class CurrentTurn(val player: Player, val coord: Coord)

fun ClientEvent.TurnEvent.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.TurnEvent.serializer(), this)
}

fun ClientEvent.GameJoined.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.GameJoined.serializer(), this)
}

fun ClientEvent.NewGame.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.NewGame.serializer(), this)
}


fun ClientEvent.GameEnded.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.GameEnded.serializer(), this)
}

fun ServerCommand.ResetCommand.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerCommand.ResetCommand.serializer(), this)
}

fun ServerCommand.JoinGameCommand.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ServerCommand.JoinGameCommand.serializer(), this)
}



fun ClientEvent.GeneralEvent.toJson(): String {
    val json = Json(JsonConfiguration.Stable)
    val jsonData = json.stringify(ClientEvent.GeneralEvent.serializer(), this)
    return jsonData
}

class ClientCommandParser() {
    companion object {

        fun getGeneralCommand(jsonStr: String): ClientEvent.GeneralEvent {
            val json = Json(JsonConfiguration.Stable)
            val obj = json.parse(ClientEvent.GeneralEvent.serializer(), jsonStr)
            return obj

        }

        fun getGameJoinedCommand(jsonStr: String): ClientEvent.GameJoined {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(ClientEvent.GameJoined.serializer(), jsonStr)

        }

        fun getTurnCommand(jsonStr: String): ClientEvent.TurnEvent {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(ClientEvent.TurnEvent.serializer(), jsonStr)

        }

        fun getGameEndedCommand(jsonStr: String): ClientEvent.GameEnded {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(ClientEvent.GameEnded.serializer(), jsonStr)

        }

        fun getMakeMove(jsonStr: String): ClientEvent.MakeMoveEvent {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(ClientEvent.MakeMoveEvent.serializer(), jsonStr)
        }

        fun getErrorCommand(jsonStr: String): ClientEvent.ErrorEvent {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(ClientEvent.ErrorEvent.serializer(), jsonStr)
        }


        fun toJson(cmd: ClientEvent.MakeMoveEvent): String {
            val json = Json(JsonConfiguration.Stable)
            return json.stringify(ClientEvent.MakeMoveEvent.serializer(), cmd)
        }

        fun toJson(cmd: ClientEvent.ErrorEvent): String {
            val json = Json(JsonConfiguration.Stable)
            return json.stringify(ClientEvent.ErrorEvent.serializer(), cmd)
        }

        fun toJson(res: Resource<ClientEvent.MakeMoveEvent>): String {

            val boxedDataSerial = Resource.serializer(ClientEvent.MakeMoveEvent.serializer())
            val json = Json(JsonConfiguration.Stable)
            val jsonData = json.stringify(boxedDataSerial, res)
            return jsonData
        }

        fun jsonFromRessource(error: Resource<String>): String {
            val boxedDataSerial = Resource.serializer(String.serializer())
            val json = Json(JsonConfiguration.Stable)
            val jsonData = json.stringify(boxedDataSerial, error)
            return jsonData
        }
    }
}


fun getServerCommandType(toString: String): ServerCommands? {

    return ServerCommands.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}

fun getClientCommandType(toString: String): ClientCommands? {
    //TODO:Find better way to get type

    return ClientCommands.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}

