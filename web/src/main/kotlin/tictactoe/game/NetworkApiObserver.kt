package tictactoe.game

import de.jensklingenberg.sheasy.model.ServerResponse
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.PlayerResponseEvent

interface NetworkApiObserver {

    fun onGameStateChanged(gameState: GameState)
    fun onPlayerEventChanged(gameResponse: PlayerResponseEvent)
    fun onError(gameJoined: ServerResponse.ErrorEvent)
}