package tictactoe.game

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import de.jensklingenberg.sheasy.model.*

class GameRepository : GameDataSource, NetworkApiObserver {

    private var activePlayerId: Int? = null
    private val warriorSubject: BehaviorSubject<List<Warrior>> = BehaviorSubject(emptyList())

    private val gameStateSubject: BehaviorSubject<GameState> = BehaviorSubject(GameState.NewGame)
    private val playerSubject: BehaviorSubject<Int> = BehaviorSubject(-1)
    private val gameApiHandler = GameApiHandler()

    override fun prepareGame() {
        gameApiHandler.start(this)
    }


    override fun observeGameState(): Observable<GameState> = gameStateSubject

    override fun observePlayer(): Observable<Int> = playerSubject

    override fun observeMap(): Observable<List<Warrior>> = warriorSubject
    override fun onMoveChar(fromCoordinate: Coordinate, toCoordinate: Coordinate) {
        console.log("onMoveChar============================0")
        val json = ServerRequest.MoveCharRequest(fromCoordinate, toCoordinate).toJson()
        gameApiHandler.sendMessage(json)
    }

    override fun onSelectedDrawWeapon(weapon: Weapon) {
        val jsonData = ServerRequest.PlayerRequest(PlayerRequestEvent.SelectedDrawWeapon(weapon)).toJson()
        gameApiHandler.sendMessage(jsonData)
    }


    override fun join() {
        val jsonData = ServerRequest.JoinGameRequest().toJson()
        gameApiHandler.sendMessage(jsonData)
    }



    override fun requestReset() {
        val jsonData = ServerRequest.ResetRequest().toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    fun onNewGame() {
        reset()
    }

    private fun reset() {
        println("RESET GAME")

    }

    override fun onTurn(turnEvent: ServerResponse.TurnEvent) {
        val turn = turnEvent.turn
        val coord = turn.coordinate
        val player = turn.player
        val symbol = player.symbol

    }

    override fun onGameStateChanged(gameState: GameState) {
        when (gameState) {
            is GameState.GameUpdate -> {
                warriorSubject.onNext(gameState.warrior)
            }
        }
        gameStateSubject.onNext(gameState)
    }

    override fun onPlayerEventChanged(gameResponse: PlayerResponseEvent) {
        when (gameResponse) {
            is PlayerResponseEvent.JOINED -> {
                activePlayerId = gameResponse.yourPlayer.id
                playerSubject.onNext(gameResponse.yourPlayer.id)
            }
        }
    }

    override fun onError(gameJoined: ServerResponse.ErrorEvent) {

    }
}

