package tictactoe.game

import de.jensklingenberg.sheasy.model.ClientEvent
import de.jensklingenberg.sheasy.model.GameState

interface NetworkApiObserver {
    fun onGameJoined(gamejoinCmd: ClientEvent.GameJoined)
    fun onTurn(turnEvent: ClientEvent.TurnEvent)
    fun onGameStateChanged(gameState: GameState)
    fun onError(gameJoined: ClientEvent.ErrorEvent)
}