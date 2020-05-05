package tictactoe.game

import de.jensklingenberg.sheasy.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
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

    fun getGameJoinedCommand(jsonStr: String): ClientEvent {
        val json = Json(JsonConfiguration.Stable)

        return json.parse(ClientEvent.serializer(), jsonStr)
    }


    private fun onMessage(messageEvent: MessageEvent) {

        val type = getClientCommandType(messageEvent.data.toString())
        console.log("TYPE:" + type.toString() + " onMessage                " + messageEvent.data.toString())

        val json = messageEvent.data.toString()

        when (type) {
            ClientCommands.JOINED -> {
                val gameJoined = ClientCommandParser.getGameJoinedCommand(json)
                observer.onGameJoined(gameJoined)
            }

            ClientCommands.TURN -> {
                val resource = ClientCommandParser.getTurnCommand(json)
                observer.onTurn(resource)
            }

            ClientCommands.STATE_CHANGED -> {
                val gameState = ClientCommandParser.getGameStateChangedCommand(json).state
                observer.onGameStateChanged(gameState)
            }

            ClientCommands.ERROR -> {
                val gameJoined = ClientCommandParser.getErrorCommand(json)
                observer.onError(gameJoined)
            }

            null -> {

            }
        }


    }

    fun sendMessage(message: String) {
        socket?.send(message)
    }
}