package ges.example.kotlinserver.game


import de.jensklingenberg.sheasy.model.*


class RpsGamePresenter(private val server: RpsGameContract.RpsGameServer) : RpsGameContract.Presenter {

    private val MAX_PLAYERS = 1
    private val playerList = mutableListOf<Player>()
    var gameState: GameState = GameState.Lobby
    var activePlayerId = 0
    private val gameArray = Array<Array<Int>>(3) { Array(3) { -1 } }
    val elementList = mutableListOf<Warrior>()


    init {
        elementList.add(Warrior(Player(0, "X", ""), Weapon.Schere(), Coord(0, 0)))
    }
    private fun checkRow(gameArray2: Array<Array<Int>>): Boolean {
        return (0..2).any { id ->
            gameArray2[id][0] != -1 &&
                    gameArray2[id][0] == gameArray2[id][2]
                    && gameArray2[id][0] == gameArray2[id][1]
        }
    }

    private fun checkCol(gameArray2: Array<Array<Int>>): Boolean {
        return (0..2).any { id ->
            gameArray2[0][id] != -1 &&
                    gameArray2[0][id] == gameArray2[1][id]
                    && gameArray2[1][id] == gameArray2[2][id]
        }
    }

    private fun checkWinner(gameArray2: Array<Array<Int>>): Boolean {
        return checkRow(gameArray2) || checkCol(gameArray2) || checkDiag(gameArray2)
    }

    private fun checkDiag(gameArray2: Array<Array<Int>>): Boolean {
        if (gameArray2[0][0] != -1 &&
                gameArray[0][0] == gameArray[2][2] &&
                gameArray[0][0] == gameArray[1][1]
        ) {
            return true
        }

        if (gameArray2[0][2] != -1 &&
                gameArray[0][2] == gameArray[2][0] &&
                gameArray[0][2] == gameArray[1][1]
        ) {
            return true
        }
        return false
    }

    private fun nextPlayer() {
        if ((activePlayerId + 1) == playerList.size) {
            activePlayerId = 0
        } else {
            activePlayerId += 1
        }
    }

    override fun onMakeMove(playerId: Int, coord: Coord) {
        if (gameState != GameState.Started) {
            sendError(playerId, ClientEvent.ErrorEvent("GAME NOT STARTED"))
            return
        }
        if (activePlayerId != playerId) {
            sendError(playerId, ClientEvent.ErrorEvent("NOT YOUR MOVE"))
            return
        }
        println("Player " + playerId + " want to make a move on : x: " + coord.x + " y: " + coord.y)
        if (gameArray[coord.y][coord.x] == -1) {
            gameArray[coord.y][coord.x] = playerId

            if (checkWinner(gameArray)) {
                sendGameStateChanged(GameState.Ended(true, activePlayerId))
            }
            nextPlayer()

            val event = ClientEvent.TurnEvent(CurrentTurn(Player(playerId, getSymbol(playerId)), coord),
                    getActivePlayer().id)

            server.sendBroadcast(event.toJson())


        } else {
            sendError(playerId, ClientEvent.ErrorEvent("CAN NOT MOVe"))
        }

    }

    private fun sendError(playerId: Int, errorEvent: ClientEvent.ErrorEvent) {
        server.sendData(playerId, ClientCommandParser.toJson(errorEvent))
    }

    override fun onReset() {
        println("RESET GAME")
        gameArray.forEachIndexed { index, columns ->
            columns.forEachIndexed { index2, rows ->
                gameArray[index][index2] = -1
            }
        }
        sendGameStateChanged(GameState.Started)
    }


    private fun getActivePlayer(): Player {
        return playerList[activePlayerId]
    }

    override fun onAddPlayer(sessionId: String) {
        val newPlayerID = playerList.size
        val player = Player(newPlayerID, getSymbol(newPlayerID), "Player$newPlayerID")
        playerList.add(player)
        server.onPlayerAdded(sessionId, player)
        val json = ClientEvent.GameJoined(player).toJson()
        server.sendData(player.id, json)
        sendGameStateChanged(GameState.Lobby)
        if (playerList.size == MAX_PLAYERS) {
            gameState=GameState.Started
            sendGameStateChanged(GameState.Started)
        }
    }

    override fun onMoveChar(playerId: Int, fromCoord: Coord, toCoord: Coord) {
        val fromChar = elementList.find { it.coord == fromCoord }

        val toChar = elementList.find { it.coord == toCoord }

        if (fromChar == null) {
            return
        } else {
            if (toChar == null) {
                elementList.remove(fromChar)
                elementList.add(fromChar.copy(coord = toCoord))
            }
        }
        val json2 = ClientEvent.GameUpdate(elementList).toJson()
        server.sendBroadcast(json2)

    }

    private fun sendGameStateChanged(gameState: GameState) {
        val json2 = ClientEvent.GameStateChanged(gameState).toJson()
        server.sendBroadcast(json2)
    }

    private fun getSymbol(playerid: Int): String {
        return when (playerid) {
            0 -> {
                "0"
            }
            1 -> {
                "1"
            }
            else -> {
                "-"
            }
        }
    }

}