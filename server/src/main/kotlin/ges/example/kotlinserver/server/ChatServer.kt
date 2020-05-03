package ges.example.kotlinserver.server

import com.squareup.moshi.Moshi
import de.jensklingenberg.sheasy.model.*
import ges.example.kotlinserver.game.GameDataSource
import ges.example.kotlinserver.game.GameRepository
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class in charge of the logic of the chat server.
 * It contains handlers to events and commands to send messages to specific users in the server.
 */
class ChatServer {
    val moshi = Moshi.Builder().build()

    val game: GameDataSource =
        GameRepository()

    /**
     * Atomic counter used to get unique user-names based on the maxiumum users the server had.
     */
    val usersCounter = AtomicInteger()

    /**
     * A concurrent map associating session IDs to user names.
     */
    val memberNames = ConcurrentHashMap<String, String>()

    /**
     * Associates a session-id to a set of websockets.
     * Since a browser is able to open several tabs and windows with the same cookies and thus the same session.
     * There might be several opened sockets for the same client.
     */
    val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    /**
     * A list of the lastest messages sent to the server, so new members can have a bit context of what
     * other people was talking about before joining.
     */
    val lastMessages = LinkedList<String>()


    init {
        game.setListener(object : GameDataSource.Listener {

            override fun onGameStateChanged(gameState: GameState) {
                when(gameState){
                    GameState.NewGame -> {

                    }
                    is GameState.Ended -> {
                        if (gameState.isWon) {
                            GlobalScope.launch {
                                val json2 = ClientEvent.GameEnded(game.getActivePlayer().id).toJson()
                                broadcast(json2)
                            }
                        }
                    }
                    GameState.Running -> {

                    }
                }
            }

        })
    }


    /**
     * Handles that a member identified with a session id and a socket joined.
     */
    suspend fun memberJoin(memberId: String, socket: WebSocketSession) {
        // Checks if this user is already registered in the server and gives him/her a temporal name if required.
        val name = memberNames.computeIfAbsent(memberId) { "user${usersCounter.incrementAndGet()}" }

        // Associates this socket to the member id.
        // Since iteration is likely to happen more frequently than adding new items,
        // we use a `CopyOnWriteArrayList`.
        // We could also control how many sockets we would allow per client here before appending it.
        // But since this is a sample we are not doing it.
        val socketList = members.computeIfAbsent(memberId) { CopyOnWriteArrayList<WebSocketSession>() }
        socketList.add(socket)

        // Only when joining the first socket for a member notifies the rest of the users.
        if (socketList.size == 1) {
            val playerId = members.keys.indexOf(memberId)

            val json = ClientEvent.GameJoined(Player(playerId, game.getSymbol(playerId))).toJson()
            //  sendTo(memberId,json)
            val json2 = ClientEvent.GameEnded(playerId).toJson()
            //  sendTo(memberId,json2)
            println("Member joined: $name.")
            broadcast("server", "Member joined: $name.")
        }

        // Sends the user the latest messages from this server to let the member have a bit context.
        val messages = synchronized(lastMessages) { lastMessages.toList() }
        for (message in messages) {
            socket.send(Frame.Text("HALLo"))
        }
    }

    /**
     * Handles a [member] idenitified by its session id renaming [to] a specific name.
     */
    suspend fun memberRenamed(member: String, to: String) {
        // Re-sets the member name.
        val oldName = memberNames.put(member, to) ?: member
        // Notifies everyone in the server about this change.
        broadcast("server", "Member renamed from $oldName to $to")
    }

    /**
     * Handles that a [member] with a specific [socket] left the server.
     */
    suspend fun memberLeft(member: String, socket: WebSocketSession) {
        // Removes the socket connection for this member
        val connections = members[member]
        connections?.remove(socket)

        // If no more sockets are connected for this member, let's remove it from the server
        // and notify the rest of the users about this event.
        if (connections != null && connections.isEmpty()) {
            val name = memberNames.remove(member) ?: member
            broadcast("server", "Member left: $name.")
        }
    }

    /**
     * Handles the 'who' command by sending the member a list of all all members names in the server.
     */
    suspend fun who(sender: String) {
        members[sender]?.send(Frame.Text(memberNames.values.joinToString(prefix = "[server::who] ")))
    }

    /**
     * Handles the 'help' command by sending the member a list of available commands.
     */
    suspend fun help(sender: String) {
        members[sender]?.send(Frame.Text("[server::help] Possible commands are: /user, /help and /who"))
    }

    /**
     * Handles sending to a [recipient] from a [sender] a [message].
     *
     * Both [recipient] and [sender] are identified by its session-id.
     */
    suspend fun sendTo(recipient: String, sender: String, message: String) {
        members[recipient]?.send(Frame.Text("[$sender] $message"))
    }

    suspend fun sendTo(recipient: String, message: String) {
        members[recipient]?.send(Frame.Text(message))
    }

    suspend fun sendError(recipient: String, sender: String, message: String) {
        members[recipient]?.send(Frame.Text("$message"))
    }

    /**
     * Handles a [message] sent from a [sender] by notifying the rest of the users.
     */
    suspend fun message(sender: String, message: String) {
        // Pre-format the message to be send, to prevent doing it for all the users or connected sockets.
        val name = memberNames[sender] ?: sender
        val formatted = "[$name] $message"

        // Sends this pre-formatted message to all the members in the server.
        broadcast(formatted)

        // Appends the message to the list of [lastMessages] and caps that collection to 100 items to prevent
        // growing too much.
        synchronized(lastMessages) {
            lastMessages.add(formatted)
            if (lastMessages.size > 100) {
                lastMessages.removeFirst()
            }
        }
    }

    /**
     * Sends a [message] to all the members in the server, including all the connections per member.
     */
    private suspend fun broadcast(message: String) {
        members.values.forEach { socket ->
            socket.send(Frame.Text(message))
        }
    }

    /**
     * Sends a [message] coming from a [sender] to all the members in the server, including all the connections per member.
     */
    private suspend fun broadcast(sender: String, message: String) {
        val name = memberNames[sender] ?: sender
        broadcast("[$name] $message")
    }

    /**
     * Sends a [message] to a list of [this] [WebSocketSession].
     */
    suspend fun List<WebSocketSession>.send(frame: Frame) {
        forEach {
            try {
                it.send(frame.copy())
            } catch (t: Throwable) {
                try {
                    it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
                } catch (ignore: ClosedSendChannelException) {
                    // at some point it will get closed
                }
            }
        }
    }

    /**
     * We received a message. Let's process it.
     */
    suspend fun receivedMessage(id: String, command: String) {
        val playerId = members.keys.indexOf(id)
        val commandType = getServerCommandType(command)

        when (commandType) {
            ServerCommands.MAKETURN -> {

                val cmd = ServerCommandParser.getMakeMove(command)

                cmd.let {
                    if (game.makeMove(playerId, cmd.coord)) {

                        val cmdJson2 = ClientEvent.TurnEvent(
                            CurrentTurn(Player(playerId, game.getSymbol(playerId)), cmd.coord),
                            game.getActivePlayer().id
                        ).toJson()
                        broadcast(cmdJson2)
                    } else {
                        val cmdJson = ClientCommandParser.toJson(ClientEvent.ErrorEvent("CAN NOT MOVe"))

                        sendError(id, "", cmdJson)
                    }
                }
            }
            ServerCommands.MESSAGE -> TODO()
            ServerCommands.RESET -> {
                game.reset()
                val json2 = ClientEvent.NewGame().toJson()
                broadcast(json2)
            }
            ServerCommands.UNKNOWN -> TODO()
            ServerCommands.ERROR -> TODO()
            ServerCommands.GENERAL -> {
                val cmd = ServerCommandParser.getGeneralCommand(command)
                when (cmd.cmdID) {
                    ServerCommands.RESET.ordinal -> {

                    }
                }

            }
            ServerCommands.EVENT -> TODO()
            ServerCommands.JOINGAME -> {
                game.playerJoined()
                val json = ClientEvent.GameJoined(Player(playerId, game.getSymbol(playerId))).toJson()
                sendTo(id, json)

            }
            null -> TODO()
        }
        // game.makeMove(playerId,)
        // We are going to handle commands (text starting with '/') and normal messages
        when {
            // The command `who` responds the user about all the member names connected to the user.
            command.startsWith("/who") -> who(id)
            // The command `user` allows the user to set its name.
            command.startsWith("/user") -> {
                // We strip the command part to get the rest of the parameters.
                // In this case the only parameter is the user's newName.
                val newName = command.removePrefix("/user").trim()
                // We verify that it is a valid name (in terms of length) to prevent abusing
                when {
                    newName.isEmpty() -> sendTo(id, "server::help", "/user [newName]")
                    newName.length > 50 -> sendTo(
                        id,
                        "server::help",
                        "new name is too long: 50 characters limit"
                    )
                    else -> memberRenamed(id, newName)
                }
            }
            // The command 'help' allows users to get a list of available commands.
            command.startsWith("/help") -> help(id)
            // If no commands matched at this point, we notify about it.
            command.startsWith("/") -> sendTo(
                id,
                "server::help",
                "Unknown command ${command.takeWhile { !it.isWhitespace() }}"
            )
            // Handle a normal message.
            else -> {
                //message(id, command)
            }
        }
    }


}
