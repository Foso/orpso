package tictactoe.ui.home

import com.badoo.reaktive.observable.subscribe
import de.jensklingenberg.sheasy.model.*
import tictactoe.game.GameDataSource
import tictactoe.game.GameRepository
import tictactoe.model.ElementImage
import kotlin.browser.window

class GameSettings {
    companion object {
        const val ROWS = 6
        const val COLS = 7
    }
}

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {
    private val elementList = mutableListOf<Warrior>()

    private val gameDataSource: GameDataSource = GameRepository()
    var selectedWarrior: Warrior? = null
    var overlayArrows: MutableList<Coordinate> = mutableListOf()

    override fun onCreate() {
        gameDataSource.prepareGame()

        gameDataSource.observeGameState().subscribe(onNext = { state ->
            when (state) {
                is GameState.NewGame -> {
                }
                is GameState.Started -> {
                    view.setgameStateText("GAME STARTED")
                }

                is GameState.Lobby -> {
                    view.setgameStateText("WAITING FOR OPPONENTS")
                }
                is GameState.Ended -> {
                    window.alert("PLAYER ${state.winnerID} HAS WON " + state.isWon)
                    view.setgameStateText("GAME ENDED")
                }

                is GameState.DrawEvent -> {
                    view.showChooseWeaponDialog()
                }
                is GameState.GameUpdate -> {

                }
            }
        })


        gameDataSource.observeMap().subscribe(onNext = { it ->
            if (it.isNotEmpty()) {
                elementList.clear()
                elementList.addAll(it)
                val imgList = elementList.map {
                    ElementImage(
                        getWeaponImagePath(it.owner.id, it.weapon),
                        it.coordinate
                    )

                }
                view.setElement(imgList)
            }
        })

        gameDataSource

        // createList(elementList)


    }


    override fun sendMessage(message: String) {
        //myWebSocket?.send(message)
    }

    override fun onCellClicked(coordinate: Coordinate) {
        val selectedOverlay = overlayArrows.find { it == coordinate }
        if (selectedOverlay != null) {
            if (selectedWarrior != null) {
                console.log("Move from: " + selectedWarrior?.coordinate + " to " + coordinate)
                gameDataSource.onMoveChar(selectedWarrior!!.coordinate, coordinate)


                overlayArrows.clear()
                view.setOverlayList(overlayArrows.map {
                    ElementImage( "",it)
                })
            }
        } else {
            overlayArrows.clear()
            val selected = elementList.find { it.coordinate == coordinate }

            if (selected?.weapon is Weapon.Trap ||
                selected?.weapon is Weapon.Flag
            // || selected?.weapon is Weapon.Hidden
            ) {
                return
            }

            if (selected != null) {
                selectedWarrior = selected

                //Left
                if (selectedWarrior?.coordinate?.x != 0) {
                    overlayArrows.add(coordinate.copy(x = coordinate.x - 1))
                }

                //Right
                if (selectedWarrior?.coordinate?.x != GameSettings.COLS) {
                    overlayArrows.add(coordinate.copy(x = coordinate.x + 1))
                }

                //Top
                if (selectedWarrior?.coordinate?.y != 0) {
                    overlayArrows.add(coordinate.copy(coordinate.y - 1))
                }

                //Bottom
                if (selectedWarrior?.coordinate?.y != GameSettings.ROWS) {
                    overlayArrows.add(coordinate.copy(coordinate.y + 1))
                }

                view.setOverlayList(overlayArrows.map {
                    ElementImage( "images/Letter_o.svg",it)
                })
            }
        }

        //gameDataSource.makeAMove(coord)
    }

    override fun reset() {
        gameDataSource.requestReset()
    }

    override fun joinGame() {
        gameDataSource.join()
    }

    override fun onWeaponChoosed(weapon: Weapon) {
        gameDataSource.onSelectedDrawWeapon(weapon)
        view.hideChooseWeaponDialog()
    }

}