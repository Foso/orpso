package tictactoe.game

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import de.jensklingenberg.sheasy.model.*

class GameRepository(private val gameApiHandler: GameApiHandler) : GameDataSource, NetworkApiObserver {

    private var activePlayerId: Player? = null
    private val warriorSubject: BehaviorSubject<List<Warrior>> = BehaviorSubject(emptyList())
    private val nextTurnSubject: BehaviorSubject<Player> = BehaviorSubject(Player(-1, ""))

    private val gameStateSubject: BehaviorSubject<GameState> = BehaviorSubject(GameState.NotConnected)
    private val playerSubject: BehaviorSubject<Int> = BehaviorSubject(-1)

    override fun getPlayer(): Player? = activePlayerId

    override fun prepareGame() {
        gameApiHandler.start(this)
    }

    override fun observeNextTurn(): Observable<Player> = nextTurnSubject
    override fun addFlag(coordinate: Coordinate) {
        console.log("AddFlag"+coordinate)
        val jsonData = ServerRequest.PlayerRequest(PlayerRequestEvent.AddFlag(coordinate)).toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun addTrap(coordinate: Coordinate) {
        console.log("AddTrap"+coordinate)
        val jsonData = ServerRequest.PlayerRequest(PlayerRequestEvent.AddTrap(coordinate)).toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun startGame() {
        val jsonData = ServerRequest.PlayerRequest(PlayerRequestEvent.StartGame()).toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun observeGameState(): Observable<GameState> = gameStateSubject
    override fun observePlayer(): Observable<Int> = playerSubject
    override fun observeMap(): Observable<List<Warrior>> = warriorSubject
    override fun onMoveChar(fromCoordinate: Coordinate, toCoordinate: Coordinate) {
        console.log("onMoveChar============================0")
        val json = ServerRequest.PlayerRequest(PlayerRequestEvent.MoveCharRequest(fromCoordinate, toCoordinate)).toJson()
        gameApiHandler.sendMessage(json)
    }

    override fun onSelectedDrawWeapon(weapon: Weapon) {
        val jsonData = ServerRequest.PlayerRequest(PlayerRequestEvent.SelectedDrawWeapon(weapon)).toJson()
        gameApiHandler.sendMessage(jsonData)
    }


    override fun join() {
        val jsonData = ServerRequest.PlayerRequest(PlayerRequestEvent.JoinGameRequest()).toJson()
        gameApiHandler.sendMessage(jsonData)
    }

    override fun requestReset() {
        val jsonData = ServerRequest.ResetRequest().toJson()
        gameApiHandler.sendMessage(jsonData)
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
                activePlayerId = gameResponse.yourPlayer
                playerSubject.onNext(gameResponse.yourPlayer.id)
            }
            is PlayerResponseEvent.NEXTPLAYER -> {
                nextTurnSubject.onNext(gameResponse.nextPlayer)
            }
        }
    }

    override fun onError(gameJoined: ServerResponse.ErrorEvent) {

    }
}

