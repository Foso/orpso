package tictactoe.game

import de.jensklingenberg.sheasy.model.ClientEvent
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.PlayerEventState

interface NetworkApiObserver {


    fun onTurn(turnEvent: ClientEvent.TurnEvent)
    fun onGameStateChanged(gameState: GameState)
    fun onPlayerEventChanged(gameState: PlayerEventState)
    fun onError(gameJoined: ClientEvent.ErrorEvent)
}