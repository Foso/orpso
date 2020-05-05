package tictactoe.game

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import de.jensklingenberg.sheasy.model.*

class GameRepository : GameDataSource, NetworkApiObserver {

    private val gameTable = Array<Array<String>>(3) { Array(3) { "-" } }
    private val gameSubject: BehaviorSubject<Array<Array<String>>> = BehaviorSubject(gameTable)
    private var activePlayerId: Int? = null
    private val gameStateSubject: BehaviorSubject<GameState> = BehaviorSubject(GameState.NewGame)
    private val playerSubject: BehaviorSubject<Int> = BehaviorSubject(-1)
    private val gameApiHandler = GameApiHandler()

    override fun prepareGame() {
        gameApiHandler.start(this)
    }
    override fun observeGameChanges(): Observable<Array<Array<String>>> = gameSubject

    override fun observeGameState(): Observable<GameState> = gameStateSubject

    override fun observePlayer(): Observable<Int> = playerSubject

    override fun join() {
        val jsonData = ServerCommand.JoinGameCommand().toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun makeAMove(coord: Coord): Completable {
        return completable {
            val makeMoveCommand = ServerCommand.MakeTurnCommand(coord)
            val jsonData = ServerCommandParser.toJson(makeMoveCommand)
            gameApiHandler.sendMessage(jsonData)
            it.onComplete()
        }
    }

    override fun requestReset() {
        val jsonData = ServerCommand.ResetCommand().toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun onGameJoined(gamejoinCmd: ClientEvent.GameJoined) {
        activePlayerId = gamejoinCmd.yourPlayer.id
        playerSubject.onNext(gamejoinCmd.yourPlayer.id)
    }

    fun onNewGame() {
        reset()
    }
    private fun reset() {
        println("RESET GAME")
        gameTable.forEachIndexed { index, columns ->
            columns.forEachIndexed { index2, _ ->
                gameTable[index][index2] = "-"
            }
        }
        gameSubject.onNext(gameTable)
    }

    override fun onTurn(turnEvent: ClientEvent.TurnEvent) {
        val turn = turnEvent.turn
        val coord = turn.coord
        val player = turn.player
        val symbol = player.symbol
        gameTable[coord.y][coord.x] = symbol
        gameSubject.onNext(gameTable)
    }

    override fun onGameStateChanged(gameState: GameState) {
        gameStateSubject.onNext(gameState)
    }

    override fun onError(gameJoined: ClientEvent.ErrorEvent) {

    }
}

