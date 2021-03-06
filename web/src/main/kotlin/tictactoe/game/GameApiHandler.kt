package tictactoe.game

import de.jensklingenberg.sheasy.model.ClientCommandParser
import de.jensklingenberg.sheasy.model.ClientCommands
import de.jensklingenberg.sheasy.model.getClientCommandType
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event

class GameApiHandler {

    private var socket: WebSocket? = null

    private lateinit var observer: NetworkApiObserver

    fun start(observer: NetworkApiObserver) {
        this.observer = observer
        socket = org.w3c.dom.WebSocket(NetworkPreferences().hostname + "tictactoe")

        socket?.onmessage = { event: Event ->
            onMessage((event as MessageEvent))
        }
    }


    private fun onMessage(messageEvent: MessageEvent) {

        val type = getClientCommandType(messageEvent.data.toString())
        console.log("TYPE:" + type.toString() + " onMessage                " + messageEvent.data.toString())

        val json = messageEvent.data.toString()

        when (type) {

            ClientCommands.STATE_CHANGED -> {
                val gameState = ClientCommandParser.getGameStateChangedCommand(json).state
                observer.onGameStateChanged(gameState)
            }

            ClientCommands.ERROR -> {
                val gameJoined = ClientCommandParser.getErrorCommand(json)
                observer.onError(gameJoined)
            }

            ClientCommands.PLAYER_EVENT -> {
                val gameState = ClientCommandParser.getPlayerEvent(json).response
                observer.onPlayerEventChanged(gameState)
            }
            ClientCommands.MESSAGE, null -> {

            }
        }


    }

    fun sendMessage(message: String) {
        socket?.send(message)
    }
}