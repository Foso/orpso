package ges.example.kotlinserver.game


import de.jensklingenberg.sheasy.model.*

class GameSettings {
    companion object {
        val ROWS = 6
        val COLS = 7
    }
}


class RpsGamePresenter(private val server: RpsGameContract.RpsGameServer) : RpsGameContract.Presenter {

    private val MAX_PLAYERS = 1
    private val playerList = mutableListOf<Player>()
    var gameState: GameState = GameState.Lobby
    var activePlayerId = 0
    private val gameArray = Array<Array<Int>>(3) { Array(3) { -1 } }
    val elementList = mutableListOf<Warrior>()


    init {
        (0 until GameSettings.COLS).forEach {
            elementList.add(Warrior(Player(1, "X", ""), Weapon.Flag(), Coord(0, it)))
            elementList.add(Warrior(Player(1, "X", ""), Weapon.Papier(), Coord(1, it)))
            elementList.add(Warrior(Player(0, "X", ""), Weapon.Schere(), Coord(GameSettings.ROWS - 2, it)))
            elementList.add(Warrior(Player(0, "X", ""), Weapon.Trap(), Coord(GameSettings.ROWS - 1, it)))
        }
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


            nextPlayer()

            val event = ClientEvent.TurnEvent(
                CurrentTurn(Player(playerId, getSymbol(playerId)), coord),
                getActivePlayer().id
            )

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


    private fun getActivePlayer(): Player = playerList[activePlayerId]

    override fun onAddPlayer(sessionId: String) {
        val newPlayerID = playerList.size
        val player = Player(newPlayerID, getSymbol(newPlayerID), "Player$newPlayerID")
        playerList.add(player)
        server.onPlayerAdded(sessionId, player)
        val json = ClientEvent.GameJoined(player).toJson()
        server.sendData(player.id, json)
        sendGameStateChanged(GameState.Lobby)
        if (playerList.size == MAX_PLAYERS) {
            gameState = GameState.Started
            sendGameStateChanged(GameState.Started)
            sendGameMap()

        }
    }

    private fun hideElements(
        playerId: Int,
        elementList: MutableList<Warrior>
    ): List<Warrior> {
        return elementList.map {

            if (it.owner.id != playerId && !it.weaponRevealed) {
                it.copy(weapon = Weapon.Hidden())
            } else {
                it
            }

        }
    }

    override fun onMoveChar(playerId: Int, fromCoord: Coord, toCoord: Coord) {
        val fromChar = elementList.find { it.coord == fromCoord }

        if(fromChar?.weapon is Weapon.Trap || fromChar?.weapon is Weapon.Flag){
            //The trap cant be moved
            return
        }

        val toChar = elementList.find { it.coord == toCoord }

        if (fromChar?.owner?.id == toChar?.owner?.id) {
            //A player cant attack himself
            return
        }

        if (fromChar == null) {
            return
        } else {
            if (toChar == null) {
                elementList.remove(fromChar)
                elementList.add(fromChar.copy(coord = toCoord))
            } else {

                when (checkWinner(attackWeapon = fromChar.weapon, defenseWeapon = toChar.weapon)) {
                    MatchState.WIN -> {
                        if(toChar.weapon is Weapon.Flag){
                            val json2 = ClientEvent.GameStateChanged(GameState.Ended(true,playerId)).toJson()
                            server.sendBroadcast(json2)
                        }
                        elementList.remove(fromChar)
                        elementList.remove(toChar)
                        elementList.add(fromChar.copy(coord = toCoord,weaponRevealed = true))
                    }
                    MatchState.LOOSE -> {
                        elementList.remove(fromChar)
                        elementList.remove(toChar)
                        elementList.add(toChar.copy(weaponRevealed = true))
                    }
                    MatchState.DRAW -> {

                    }
                }
            }
        }
        sendGameMap()

    }

    private fun sendGameMap() {
        playerList.forEach {
            val newEle = hideElements(it.id, elementList)
            val json2 = ClientEvent.GameUpdate(newEle).toJson()
            server.sendData(it.id, json2)
        }
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