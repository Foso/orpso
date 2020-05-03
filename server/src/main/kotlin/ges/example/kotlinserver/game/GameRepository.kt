package ges.example.kotlinserver.game


import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.GameState
import de.jensklingenberg.sheasy.model.Player
import de.jensklingenberg.sheasy.model.Status

class GameRepository() : GameDataSource {

    val MAX_PLAYERS = 2

    val PLAYER1 = 0
    val PLAYER2 = 1

    val playerList = mutableListOf<Player>()

    var activePlayerId = 0

    private var listener1: GameDataSource.Listener? = null

    private val gameArray = Array<Array<Int>>(3) { Array(3) { -1 } }
    override fun setListener(listener: GameDataSource.Listener) {
        listener1 = listener
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

    override fun makeMove(playerId: Int, coord: Coord): Boolean {
        if (activePlayerId != playerId) {
            return false
        }
        println("Player " + playerId + " want to make a move on : x: " + coord.x + " y: " + coord.y)
        return if (gameArray[coord.y][coord.x] == -1) {
            gameArray[coord.y][coord.x] = playerId
            // liste?.onEmitMove(playerId,coord)
            if (checkWinner(gameArray)) {
                listener1?.onGameStateChanged(GameState.Ended(true))
            }
            nextPlayer()
            true
        } else {
            false
        }

    }

    override fun reset() {
        println("RESET GAME")
        gameArray.forEachIndexed { index, columns ->
            columns.forEachIndexed { index2, rows ->
                gameArray[index][index2] = -1
            }
        }


    }

    override fun getActivePlayer(): Player {
        return playerList[activePlayerId]
    }

    override fun playerJoined(): Status {
        val newPlayerID = playerList.size
        playerList.add(Player(newPlayerID, getSymbol(newPlayerID), "Player$newPlayerID"))
        return Status.SUCCESS
    }

    override fun getSymbol(playerid: Int): String {
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