package tictactoe.ui.home

import tictactoe.game.GameDataSource
import tictactoe.game.GameRepository
import com.badoo.reaktive.completable.subscribe
import com.badoo.reaktive.observable.subscribe
import de.jensklingenberg.sheasy.model.Coord
import de.jensklingenberg.sheasy.model.GameState
import kotlin.browser.window


class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {

    private val gameDataSource: GameDataSource = GameRepository()

    override fun onCreate() {
        gameDataSource.prepareGame()

        gameDataSource.observeGameState().subscribe(onNext = {state->
           when(state){
               GameState.NewGame -> {}
               GameState.Started -> {
                   view.setgameStateText("GAME STARTED")
               }
               is GameState.Ended -> {
                   window.alert("PLAYER ${state.winnerID} HAS WON "+state.isWon)
                   view.setgameStateText("GAME ENDED")
               }
               is GameState.Lobby -> {
                   view.setgameStateText("WAITING FOR OPPONENTS")
               }
           }
        })

        gameDataSource.observeGameChanges().subscribe(onNext = {
            view.setGameData(it)
        })

        gameDataSource.observePlayer().subscribe(onNext = {
            if (it != -1) {
                view.setPlayerId(it)
            }
        })
    }

    override fun sendMessage(message: String) {
        //myWebSocket?.send(message)
    }

    override fun onCellClicked(coord: Coord) {
        gameDataSource.makeAMove(coord).subscribe { }
    }

    override fun reset() {
        gameDataSource.requestReset()
    }

    override fun joinGame() {
        gameDataSource.join()
    }

}