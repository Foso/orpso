package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


enum class MatchState{
    WIN,LOOSE,DRAW
}

fun checkWinner(attackWeapon:Weapon,defenseWeapon:Weapon): MatchState {

       return when(defenseWeapon){
            is Weapon.Scissors -> {
                when (attackWeapon) {
                    is Weapon.Rock -> {
                        MatchState.WIN
                    }
                    is Weapon.Scissors -> {
                        MatchState.DRAW
                    }
                    else -> {
                        MatchState.LOOSE
                    }
                }
            }
            is Weapon.Papier -> {
                when (attackWeapon) {
                    is Weapon.Scissors -> {
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

enum class ClientCommands {
    JOINED, ERROR, TURN, STATE_CHANGED, MESSAGE, PLAYER_EVENT
}
@Serializable
sealed class PlayerEventState{
    @Serializable
    class JOINED(val yourPlayer: Player) : PlayerEventState()
}

@Serializable
sealed class ClientEvent(val id: Int) {

    @Serializable
    class PlayerEvent(val state: PlayerEventState) : ClientEvent(ClientCommands.PLAYER_EVENT.ordinal)

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

fun ClientEvent.PlayerEvent.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.PlayerEvent.serializer(), this)
}

fun ClientEvent.GameStateChanged.toJson(): String {
    return Json(JsonConfiguration.Stable).stringify(ClientEvent.GameStateChanged.serializer(), this)
}


fun getClientCommandType(toString: String): ClientCommands? {

    return ClientCommands.values().firstOrNull() {
        toString.startsWith("{\"id\":${it.ordinal}")
    }
}