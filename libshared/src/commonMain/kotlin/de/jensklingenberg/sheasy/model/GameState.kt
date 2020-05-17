package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

@Serializable
sealed class GameState {

    /**
     * Initial State for clients
     */
    @Serializable
    object NotConnected : GameState()

    /**
     * A player is waiting for other players
     */
    @Serializable
    object Matchmaking : GameState()

    @Serializable
    object Lobby : GameState()

    @Serializable
    object Started : GameState()

    @Serializable
    object DrawEvent : GameState()

    @Serializable
    class GameUpdate(val warrior: List<Warrior>) : GameState()

    @Serializable
    class Ended(val isWon: Boolean, val winnerID: Int) : GameState()

}