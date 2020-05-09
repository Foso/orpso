package tictactoe.game

import de.jensklingenberg.sheasy.model.ClientEvent
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.Warrior

interface NetworkApiObserver {
    fun onGameUpdated(warrios: List<Warrior>)
    fun onGameJoined(gamejoinCmd: ClientEvent.GameJoined)
    fun onTurn(turnEvent: ClientEvent.TurnEvent)
    fun onGameStateChanged(gameState: GameState)
    fun onError(gameJoined: ClientEvent.ErrorEvent)
}