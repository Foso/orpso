package de.jensklingenberg.sheasy.model

import kotlinx.serialization.Serializable

@Serializable
sealed class GameState {
    @Serializable
    object NewGame : GameState()
    @Serializable
    object Lobby : GameState()
    @Serializable
    object Started : GameState()

    @Serializable
    object DrawEvent : GameState()

    @Serializable
    class GameUpdate(val warrior: List<Warrior>): GameState()

    @Serializable
    class Ended(val isWon: Boolean,val winnerID: Int) : GameState()

}