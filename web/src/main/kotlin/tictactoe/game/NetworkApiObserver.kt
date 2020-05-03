package tictactoe.game

import de.jensklingenberg.sheasy.model.ClientEvent

interface NetworkApiObserver {
    fun onGameJoined(gamejoinCmd: ClientEvent.GameJoined)
    fun onNewGame()
    fun onTurn(turnEvent: ClientEvent.TurnEvent)
    //fun onMakeMove(makeMove: ClientEvent.MakeMoveEvent)
    fun onGameEnded(gameEnded: ClientEvent.GameEnded)
}