package tictactoe.ui.home

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.subscribe
import de.jensklingenberg.sheasy.model.*
import tictactoe.game.GameDataSource
import tictactoe.model.ElementImage
import kotlin.browser.window

class GameSettings {
    companion object {
        const val ROWS = 6
        const val COLS = 7
    }
}

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {

    private var playerId = -1
    private val elementList = mutableListOf<Warrior>()
    private val gameDataSource: GameDataSource = Application.gameDataSource
    private var selectedWarrior: Warrior? = null
    private var overlayArrows: MutableList<Coordinate> = mutableListOf()
    private val compositeDisposable = CompositeDisposable()
    var flagIsSet=false
    var trapIsSet=false
    override fun onCreate() {
        gameDataSource.prepareGame()

        gameDataSource.observePlayer().subscribe {
            playerId = it
            view.setPlayerId(it)
        }

        gameDataSource.observeNextTurn().subscribe {
            if(it.id == playerId){
                console.log("HE"+it.id)
                view.setgameStateText("ITS YOUR TURN")
            }else{
                view.setgameStateText("WAITING FOR PLAYER ${it.id}")
            }

        }.addTo(compositeDisposable)



        gameDataSource.observeGameState().subscribe(onNext = { state ->
            console.log("TUTU"+state)
            when (state) {
                is GameState.Matchmaking -> {
                    view.setgameStateText("WAITING FOR OPPONENTS")

                }
                is GameState.Started -> {
                    view.setgameStateText("GAME STARTED")
                }

                is GameState.Lobby -> {
                    view.setgameStateText("SET YOUR FLAG")
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
                is GameState.NotConnected -> {
                    view.setgameStateText("Not Connected")
                }
            }
        })


        gameDataSource.observeMap().subscribe(onNext = { it ->
            if (it.isNotEmpty()) {

                elementList.clear()
                elementList.addAll(it)
                if(!flagIsSet){
                    flagIsSet=  elementList.any { it.owner.id == playerId && it.weapon is Weapon.Flag }
                    console.log("FLAG EXIST"+flagIsSet)

                }
                if(!trapIsSet){
                    trapIsSet=  elementList.any { it.owner.id == playerId && it.weapon is Weapon.Trap }
                    console.log("Trap EXIST"+trapIsSet)
                }

                val imgList = elementList.map {
                    ElementImage(
                        getWeaponImagePath(it.owner.id, it.weapon),
                        it.coordinate
                    )

                }
                view.setElement(imgList)
            }
        })

        (0 until GameSettings.COLS).forEach {
            elementList.add(Warrior(Player(1, "X", ""), Weapon.Hidden, Coordinate(0, it)))
            elementList.add(Warrior(Player(1, "X", ""), Weapon.Hidden, Coordinate(1, it)))
            elementList.add(Warrior(Player(0, "X", ""), Weapon.Hidden, Coordinate(GameSettings.ROWS - 2, it)))
            elementList.add(Warrior(Player(0, "X", ""), Weapon.Hidden, Coordinate(GameSettings.ROWS - 1, it)))
        }
        val imgList = elementList.map {
            ElementImage(
                getWeaponImagePath(it.owner.id, it.weapon),
                it.coordinate
            )

        }
        view.setElement(imgList)
    }


    override fun sendMessage(message: String) {
        //myWebSocket?.send(message)
    }

    override fun onCellClicked(coordinate: Coordinate) {
        if(!flagIsSet){
            gameDataSource.addFlag(coordinate)
            return
        }

        if(!trapIsSet){
            gameDataSource.addTrap(coordinate)
            return
        }

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

    override fun shuffleElements() {

    }

    override fun startGame() {
        gameDataSource.startGame()
    }

}