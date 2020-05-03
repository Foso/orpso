package de.jensklingenberg.sheasy.model

sealed class GameState() {
    object NewGame : GameState()
    class Ended(val isWon: Boolean) : GameState()
    object Running : GameState()
}