package tictactoe.game

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import de.jensklingenberg.sheasy.model.*
import kotlin.browser.window

class GameRepository : GameDataSource, NetworkApiObserver {

    private val gameTable = Array<Array<String>>(3) { Array(3) { "-" } }
    private val gameSubject: BehaviorSubject<Array<Array<String>>> = BehaviorSubject(gameTable)
    private var activePlayerId: Int? = null
    private val winSubject: BehaviorSubject<GameState> = BehaviorSubject(GameState.NewGame)
    private val playerSubject: BehaviorSubject<Int> = BehaviorSubject(-1)
    private val gameApiHandler = GameApiHandler()

    override fun prepareGame() {
        console.log("PREPARE")
        gameApiHandler.start(this)
    }

    override fun join() {
        winSubject.onNext(GameState.Running)
        val jsonData =
            ServerCommand.JoinGameCommand().toJson()
        console.log("HERE*"+jsonData)
        gameApiHandler.sendMessage(jsonData)
    }

    override fun makeAMove(coord: Coord): Completable {
        return completable {
            val makeMoveCommand =
                ServerCommand.MakeTurnCommand(coord)
            val jsonData =
                ServerCommandParser.toJson(makeMoveCommand)
            gameApiHandler.sendMessage(jsonData)
            it.onComplete()
        }
    }

    override fun observeGameChanges(): Observable<Array<Array<String>>> {
        return gameSubject
    }

    override fun observeGameState(): Observable<GameState> {
        return winSubject
    }

    override fun requestReset() {
        console.log("RESET")
        val jsonData = ServerCommand.ResetCommand().toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun observePlayer(): Observable<Int> {
        return playerSubject
    }

    override fun onGameJoined(gamejoinCmd: ClientEvent.GameJoined) {
        activePlayerId = gamejoinCmd.player.id
        playerSubject.onNext(gamejoinCmd.player.id)
        console.log("JOINED PlayerID:" + gamejoinCmd.player.id)
    }

    override fun onNewGame() {
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
        console.log("onTurn" + turnEvent)


        val turn = turnEvent.turn
        val coord = turn.coord
        val player = turn.player
        val symbol = player.symbol
        gameTable[coord.y][coord.x] = symbol
        gameSubject.onNext(gameTable)
    }


    override fun onGameEnded(gameEnded: ClientEvent.GameEnded) {
        window.alert("GAME ENDED; WINNER IS PLAYER: " + gameEnded.winnerId)
    }
}

