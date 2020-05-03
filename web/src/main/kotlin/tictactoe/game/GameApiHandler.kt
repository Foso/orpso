package tictactoe.game

import de.jensklingenberg.sheasy.model.ClientCommandParser
import de.jensklingenberg.sheasy.model.ClientCommands
import de.jensklingenberg.sheasy.model.getClientCommandType
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event

class GameApiHandler {
    var socket: WebSocket? = null

    private lateinit var observer: NetworkApiObserver

    fun start(observer: NetworkApiObserver) {
        this.observer = observer

        socket = org.w3c.dom.WebSocket("ws://localhost:23567/tictactoe")

        socket?.onmessage = { event: Event ->
            onMessage((event as MessageEvent))
        }
    }

    private fun onMessage(messageEvent: MessageEvent) {
        val type = getClientCommandType(messageEvent.data.toString())
        console.log(type.toString()+" onMessage                " + messageEvent.data.toString())

        val json = messageEvent.data.toString()

        when (type) {
            ClientCommands.NEW_GAME -> {
                observer.onNewGame()
            }
            ClientCommands.JOINED -> {
                val gameJoined =
                    ClientCommandParser.getGameJoinedCommand(
                        json
                    )
                observer.onGameJoined(gameJoined)
            }

            ClientCommands.TURN -> {
                console.log("TURN: "+json)
                val resource =
                    ClientCommandParser.getTurnCommand(
                        json
                    )
                observer.onTurn(resource)
            }

            ClientCommands.GAME_ENDED -> {
                val gameEnded =
                    ClientCommandParser.getGameEndedCommand(
                        json
                    )
                observer.onGameEnded(gameEnded)
            }

            ClientCommands.ERROR -> {

            }

            ClientCommands.UNKNOWN -> {

            }

            null -> {

            }

        }


    }

    fun sendMessage(message: String) {
        socket?.send(message)
    }
}