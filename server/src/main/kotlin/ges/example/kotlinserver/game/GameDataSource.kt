package ges.example.kotlinserver.game

import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.Player
import de.jensklingenberg.sheasy.model.Status

interface GameDataSource {

    interface Listener {
        fun onGameStateChanged(gameState: GameState)
    }

    fun setListener(listener: Listener)
    fun makeMove(playerId: Int, coord: Coord): Boolean
    fun reset()

    fun getActivePlayer():Player
    fun playerJoined() : Status
    fun getSymbol(playerid: Int): String
}

