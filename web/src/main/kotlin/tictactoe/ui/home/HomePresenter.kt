package tictactoe.ui.home

import tictactoe.game.GameDataSource
import tictactoe.game.GameRepository
import com.badoo.reaktive.completable.subscribe
import com.badoo.reaktive.observable.subscribe
import de.jensklingenberg.sheasy.model.Coord


class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {

    private val gameDataSource: GameDataSource = GameRepository()

    override fun onCreate() {
        console.log("Prensern.onCreate")
        gameDataSource.prepareGame()

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