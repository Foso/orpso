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
    var attacker: Attacker? = null
    private val elementList = mutableListOf<Warrior>()
    var attackerDrawWeaponPlayer: Weapon? = null
    var defenderDrawWeaponPlayer: Weapon? = null

    init {
        (0 until GameSettings.COLS).forEach {
            elementList.add(Warrior(Player(1, "X", ""), Weapon.Flag, Coordinate(0, it)))
            elementList.add(Warrior(Player(1, "X", ""), Weapon.Scissors, Coordinate(1, it)))
            elementList.add(Warrior(Player(0, "X", ""), Weapon.Scissors, Coordinate(GameSettings.ROWS - 2, it)))
            elementList.add(Warrior(Player(0, "X", ""), Weapon.Trap, Coordinate(GameSettings.ROWS - 1, it)))
        }
    }

    private fun nextPlayer() {
        if ((activePlayerId + 1) == playerList.size) {
            activePlayerId = 0
        } else {
            activePlayerId += 1
        }
    }


    private fun sendError(playerId: Int, errorEvent: ServerResponse.ErrorEvent) {
        server.sendData(playerId, ClientCommandParser.toJson(errorEvent))
    }

    override fun onReset() {
        println("RESET GAME")

        sendGameStateChanged(GameState.Started)
    }


    private fun getActivePlayer(): Player = playerList[activePlayerId]

    override fun onAddPlayer(sessionId: String) {
        val newPlayerID = playerList.size
        val player = Player(newPlayerID, getSymbol(newPlayerID), "Player$newPlayerID")
        playerList.add(player)
        server.onPlayerAdded(sessionId, player)

        val json2 = ServerResponse.PlayerEvent(PlayerResponseEvent.JOINED(player)).toJson()
        server.sendData(player.id, json2)
        sendGameStateChanged(GameState.Lobby)
        if (playerList.size == MAX_PLAYERS) {
            gameState = GameState.Started
            sendGameStateChanged(GameState.Started)
            sendGameMap()

        }
    }

    private fun hideElements(
        playerId: Int,
        elementList: List<Warrior>
    ): List<Warrior> {
        return elementList.map {
            if (it.owner.id != playerId && !it.weaponRevealed) {
                it.copy(weapon = Weapon.Hidden)
            } else {
                it
            }

        }
    }

    override fun onReceivedSelectedDrawWeapon(playerId: Int, weapon: Weapon) {
        defenderDrawWeaponPlayer = Weapon.Paper
        if (playerId == attacker?.playerId) {
            attackerDrawWeaponPlayer = weapon
        } else {
            defenderDrawWeaponPlayer = weapon
        }

        if (attackerDrawWeaponPlayer != null && defenderDrawWeaponPlayer != null) {
            elementList.replaceAll {
                when (it.coordinate) {
                    attacker?.fromCoordinate -> {
                        it.copy(weapon = attackerDrawWeaponPlayer!!)
                    }
                    attacker?.toCoordinate -> {
                        it.copy(weapon = defenderDrawWeaponPlayer!!)
                    }
                    else -> {
                        it
                    }
                }
            }
            attacker?.let {
                gameState = GameState.Started
                attackerDrawWeaponPlayer = null
                defenderDrawWeaponPlayer = null
                onMoveChar(it.playerId, it.fromCoordinate, it.toCoordinate)
            }
        }
    }

    override fun onMoveChar(playerId: Int, fromCoordinate: Coordinate, toCoordinate: Coordinate) {
        if (gameState != GameState.Started) {
            return
        }

        val fromChar = elementList.find { it.coordinate == fromCoordinate }

        if (fromChar?.weapon is Weapon.Trap || fromChar?.weapon is Weapon.Flag) {
            //The trap cant be moved
            return
        }

        val toChar = elementList.find { it.coordinate == toCoordinate }

        if (fromChar?.owner?.id == toChar?.owner?.id) {
            //A player cant attack himself
            return
        }

        if (fromChar == null) {
            return
        } else {
            if (toChar == null) {
                elementList.remove(fromChar)
                elementList.add(fromChar.copy(coordinate = toCoordinate))
                sendGameMap()
            } else {

                when (checkWinner(attackWeapon = fromChar.weapon, defenseWeapon = toChar.weapon)) {
                    MatchState.WIN -> {
                        handleMatchWin(toChar, playerId, fromChar, toCoordinate)

                    }
                    MatchState.LOOSE -> {
                        elementList.remove(fromChar)
                        elementList.remove(toChar)
                        elementList.add(toChar.copy(weaponRevealed = true))
                        sendGameMap()

                    }
                    MatchState.DRAW -> {
                        attacker = Attacker(playerId, fromCoordinate, toCoordinate)
                        gameState = GameState.DrawEvent
                        val json2 = ServerResponse.GameStateChanged(GameState.DrawEvent).toJson()
                        server.sendBroadcast(json2)
                    }
                }
            }
        }

    }

    private fun handleMatchWin(
        toChar: Warrior,
        playerId: Int,
        fromChar: Warrior,
        toCoordinate: Coordinate
    ) {
        if (toChar.weapon is Weapon.Flag) {
            val json2 = ServerResponse.GameStateChanged(GameState.Ended(true, playerId)).toJson()
            server.sendBroadcast(json2)
        }
        elementList.remove(fromChar)
        elementList.remove(toChar)
        elementList.add(fromChar.copy(coordinate = toCoordinate, weaponRevealed = true))
        sendGameMap()
    }

    private fun sendGameMap() {
        playerList.forEach {
            val newEle = hideElements(it.id, elementList)

            val json = ServerResponse.GameStateChanged(GameState.GameUpdate(newEle)).toJson()
            server.sendData(it.id, json)
        }
    }

    private fun sendGameStateChanged(gameState: GameState) {
        val json2 = ServerResponse.GameStateChanged(gameState).toJson()
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

class Attacker(val playerId: Int, val fromCoordinate: Coordinate, val toCoordinate: Coordinate)