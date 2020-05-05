package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


@Serializable
enum class ServerCommands {
    MAKETURN,
    MESSAGE,
    ERROR,
    UNKNOWN,
    RESET,
    JOINGAME
}


@Serializable
sealed class ServerCommand(val id: Int) {

    @Serializable
    class JoinGameCommand : ServerCommand(ServerCommands.JOINGAME.ordinal)

    @Serializable
    class MakeTurnCommand(val coord: Coord) : ServerCommand(
        ServerCommands.MAKETURN.ordinal)

    @Serializable
    class ResetCommand : ServerCommand(ServerCommands.RESET.ordinal)

    @Serializable
    class MessageCommand : ServerCommand(ServerCommands.MESSAGE.ordinal)

}

class ServerCommandParser {
    companion object {
        fun getMakeMove(jsonStr: String): ServerCommand.MakeTurnCommand {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(ServerCommand.MakeTurnCommand.serializer(), jsonStr)

        }

        fun toJson(cmd: ServerCommand.MakeTurnCommand): String {
            val json = Json(JsonConfiguration.Stable)
            return json.stringify(ServerCommand.MakeTurnCommand.serializer(), cmd)
        }

    }
}