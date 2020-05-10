package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
data class Warrior(val owner: Player, val weapon: Weapon, val coord: Coord, val weaponRevealed: Boolean = false)

fun getWeaponImagePath(id: Int, weapon: Weapon): String {

    fun schere(id: Int): String = when (id) {
        0 -> "images/scissors_blue.svg"
        1 -> "images/scissors_red.svg"
        else -> ""
    }

    fun rock(id: Int): String = when (id) {
        0 -> "images/rock_blue.svg"
        1 -> "images/rock_red.svg"
        else -> ""

    }

    fun hidden(id: Int): String {
        return when (id) {
            0 -> "images/player_blue.svg"
            1 -> "images/player_red.svg"
            else -> ""
        }
    }

    fun paper(id: Int): String {
        return when (id) {
            0 -> "images/paper_blue.svg"
            1 -> "images/paper_red.svg"
            else -> ""
        }
    }

    fun flag(id: Int): String {
        return when (id) {
            0 -> "images/flag_blue.svg"
            1 -> "images/flag_red.svg"
            else -> ""
        }
    }

    fun trap(id: Int): String {
        return when (id) {
            0 -> "images/trap_blue.svg"
            1 -> "images/trap_red.svg"
            else -> ""
        }
    }

    return when (weapon) {
        is Weapon.Schere -> schere(id)
        is Weapon.Papier -> paper(id)
        is Weapon.Rock -> rock(id)
        is Weapon.Trap -> trap(id)
        is Weapon.Flag -> flag(id)
        is Weapon.Hidden -> hidden(id)
    }
}

enum class MatchState{
    WIN,LOOSE,DRAW
}

fun checkWinner(attackWeapon:Weapon,defenseWeapon:Weapon): MatchState {

       return when(defenseWeapon){
            is Weapon.Schere -> {
                when (attackWeapon) {
                    is Weapon.Rock -> {
                        MatchState.WIN
                    }
                    is Weapon.Schere -> {
                        MatchState.DRAW
                    }
                    else -> {
                        MatchState.LOOSE
                    }
                }
            }
            is Weapon.Papier -> {
                when (attackWeapon) {
                    is Weapon.Schere -> {
                        MatchState.WIN
                    }
                    is Weapon.Papier -> {
                        MatchState.DRAW
                    }
                    else -> {
                        MatchState.LOOSE
                    }
                }

            }
            is Weapon.Rock -> {
                when (attackWeapon) {
                    is Weapon.Papier -> {
                        MatchState.WIN
                    }
                    is Weapon.Rock -> {
                        MatchState.DRAW
                    }
                    else -> {
                        MatchState.LOOSE
                    }
                }
            }
            is Weapon.Trap -> {
                MatchState.LOOSE
            }
            is Weapon.Flag -> {
                MatchState.WIN
            }
            is Weapon.Hidden -> {
                MatchState.WIN
            }
        }
}

@Serializable
sealed class Weapon() {
    @Serializable
    class Schere : Weapon()

    @Serializable
    class Papier : Weapon()

    @Serializable
    class Rock : Weapon()

    @Serializable
    class Trap : Weapon()

    @Serializable
    class Flag : Weapon()

    @Serializable
    class Hidden : Weapon()
}

enum class ClientCommands {
    JOINED, ERROR, TURN, STATE_CHANGED, MESSAGE, UPDATE
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

    @Serializable
    class GameUpdate(val warrior: List<Warrior>) : ClientEvent(ClientCommands.UPDATE.ordinal)
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

fun ClientEvent.GameUpdate.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.GameUpdate.serializer(), this)
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

        fun getUpdateCommand(jsonStr: String): ClientEvent.GameUpdate {
            return json.parse(ClientEvent.GameUpdate.serializer(), jsonStr)
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